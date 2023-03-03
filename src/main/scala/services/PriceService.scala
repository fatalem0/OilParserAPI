package services

import exceptions.Exceptions.NoSuchDateException
import utils.DateUtils.{isDateInPeriod, parseToLocalDate}

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object PriceService {

  case class CsvRecord(startDate: LocalDate, endDate: LocalDate, averagePrice: String)

  /**
   * Gets an average price for the given 'date'
   */
  def getPriceByDate(date: String)(implicit csv: Seq[CsvRecord]): Either[NoSuchDateException, String] = {
    val parsedDate = parseToLocalDate(date)

    csv.find(csvLine => isDateInPeriod(parsedDate, csvLine.startDate, csvLine.endDate)) match {
      case Some(line) => Right(line.averagePrice)
      case _ => Left(NoSuchDateException("The date is not exist"))
    }
  }

  /**
   * Selects an interval of periods between the period which contains
   * the 'dateFrom' and the period which contains the 'dateTo'
   */
  private def getCsvRecordsFromPeriod(data: Seq[CsvRecord],
                                      dateFrom: LocalDate,
                                      dateTo: LocalDate): Seq[CsvRecord] = {
    data.slice(
      getIdxOfDatePeriod(data, dateFrom),
      getIdxOfDatePeriod(data, dateTo) + 1
    )
  }

  /**
   * Gets the index of a date that lies in some period from 'data'
   */
  private def getIdxOfDatePeriod(data: Seq[CsvRecord], date: LocalDate): Int =
    data.indexWhere(row => isDateInPeriod(date, row.startDate, row.endDate))

  /**
   * Gets the average price between the 'fromDate' and the 'toDate'
   */
  def getAvgPriceByPeriod(fromDate: String, toDate: String)
                         (implicit csv: Seq[CsvRecord]): Either[NoSuchDateException, String] = {

    // TODO: test case - from=15.мар.13&to=28.май.13
    val parsedFromDate: LocalDate = parseToLocalDate(fromDate)
    val parsedToDate: LocalDate = parseToLocalDate(toDate)

    val csvRecordsFromPeriod: Seq[CsvRecord] =
      getCsvRecordsFromPeriod(csv, parsedFromDate, parsedToDate)

    if (csvRecordsFromPeriod.isEmpty) {
      Left(NoSuchDateException("Wrong date period"))
    }

//    csvRecordsFromPeriod.foreach(println)
//    println(parsedFromDate)
//    println(csvRecordsFromPeriod.head.endDate.plusDays(1))

    val daysUntilEndFromDataPeriod: Long = ChronoUnit.DAYS.between(parsedFromDate,
      csvRecordsFromPeriod.head.endDate.plusDays(1)
    )

//    println(daysUntilEndFromDataPeriod)

    val daysUntilEndToDataPeriod: Long = ChronoUnit.DAYS.between(
      csvRecordsFromPeriod.last.startDate,
      parsedToDate.plusDays(1)
    )

//    println(daysUntilEndToDataPeriod)

    /** The average price between the 'fromDate' and the end of the period where the 'fromDate' is */
    val averagePricePrefix: Double = csvRecordsFromPeriod.head.averagePrice.toDouble / ChronoUnit.DAYS.between(
      csvRecordsFromPeriod.head.startDate,
      csvRecordsFromPeriod.head.endDate.plusDays(1)
    ) * daysUntilEndFromDataPeriod

//    println(averagePricePrefix)

    /** The average price between the start of the period where the 'toDate' is and the 'toDate' */
    val averagePriceSuffix: Double = csvRecordsFromPeriod.last.averagePrice.toDouble / ChronoUnit.DAYS.between(
      csvRecordsFromPeriod.last.startDate,
      csvRecordsFromPeriod.last.endDate.plusDays(1)
    ) * daysUntilEndToDataPeriod

//    println(averagePriceSuffix)

    val averageRes: Double = if (csvRecordsFromPeriod.size > 2) {
      val period: Seq[CsvRecord] = csvRecordsFromPeriod.drop(1).dropRight(1)

      period.map(_.averagePrice.toDouble).sum + averagePricePrefix + averagePriceSuffix
    } else {
      averagePricePrefix + averagePriceSuffix
    }

//    println(averageRes)

    Right(averageRes.toString)

  }

}
