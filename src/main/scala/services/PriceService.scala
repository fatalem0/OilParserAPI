package services

import data.{CsvRecord, MinAndMaxPrices}
import exceptions.{NoSuchDateException, WrongDateFormat}
import io.circe.Json
import io.circe.syntax._
import utils.DateUtils.{isDateInPeriod, parseRandomDateToLocalDate}

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object PriceService {

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
   * Gets an average price for the given 'date'
   */
  def getPriceByDate(date: String)(implicit csv: Seq[CsvRecord]): Either[Throwable, String] = {
    val parsedDate: Either[Throwable, LocalDate] = parseRandomDateToLocalDate(date)

    parsedDate match {
      case Left(ex) => Left(ex)

      case Right(rightParsedDate) =>
        csv.find(csvLine => isDateInPeriod(rightParsedDate, csvLine.startDate, csvLine.endDate)) match {
          case Some(line) => Right(line.averagePrice)
          case _          => Left(NoSuchDateException("The date is not exist"))
        }
    }
  }

  /** Processes correctly parsed dates */
  private def processParsedDates(fromDate: LocalDate, toDate: LocalDate)
                     (implicit csv: Seq[CsvRecord]): Either[Throwable, String] = {

    val csvRecordsFromPeriod: Seq[CsvRecord] =
      getCsvRecordsFromPeriod(csv, fromDate, toDate)

    if (csvRecordsFromPeriod.isEmpty) {
      Left(NoSuchDateException("Wrong date period"))
    } else {

      val daysUntilEndFromDataPeriod: Long = ChronoUnit.DAYS.between(fromDate,
        csvRecordsFromPeriod.head.endDate.plusDays(1)
      )

      val daysUntilEndToDataPeriod: Long = ChronoUnit.DAYS.between(
        csvRecordsFromPeriod.last.startDate,
        toDate.plusDays(1)
      )

      /** The average price between the 'fromDate' and the end of the period where the 'fromDate' is */
      val averagePricePrefix: Double =
        csvRecordsFromPeriod.head.averagePrice.toDouble / ChronoUnit.DAYS.between(
          csvRecordsFromPeriod.head.startDate,
          csvRecordsFromPeriod.head.endDate.plusDays(1)
        ) * daysUntilEndFromDataPeriod


      /** The average price between the start of the period where the 'toDate' is and the 'toDate' */
      val averagePriceSuffix: Double =
        csvRecordsFromPeriod.last.averagePrice.toDouble / ChronoUnit.DAYS.between(
          csvRecordsFromPeriod.last.startDate,
          csvRecordsFromPeriod.last.endDate.plusDays(1)
        ) * daysUntilEndToDataPeriod

      val averageRes: Double = if (csvRecordsFromPeriod.size > 2) {
        val period: Seq[CsvRecord] = csvRecordsFromPeriod.drop(1).dropRight(1)

        period.map(_.averagePrice.toDouble).sum + averagePricePrefix + averagePriceSuffix
      } else {
        averagePricePrefix + averagePriceSuffix
      }

      Right(averageRes.toString)

    }
  }

  /**
   * Gets the average price between the 'fromDate' and the 'toDate'
   */
  def getAvgPriceByPeriod(fromDate: String, toDate: String)
                         (implicit csv: Seq[CsvRecord]): Either[Throwable, String] = {

    val parsedFromDate: Either[WrongDateFormat, LocalDate] = parseRandomDateToLocalDate(fromDate)
    val parsedToDate: Either[WrongDateFormat, LocalDate] = parseRandomDateToLocalDate(toDate)

    (parsedFromDate, parsedToDate) match {
      case (Left(ex), _) =>
        Left(WrongDateFormat(ex.message))
      case (_, Left(ex)) =>
        Left(WrongDateFormat(ex.message))
      case (Right(parsedRightFromDate), Right(parsedRightToDate)) =>
        processParsedDates(parsedRightFromDate, parsedRightToDate) match {
          case Left(ex) => Left(ex)
          case Right(v) => Right(v)
        }
    }

  }

  /**
   * Gets the maximum average price and the minimal average price between the 'fromDate' and the 'toDate'
   * and returns the 'MinAndMaxPrices' in JSON format
   */
  def getMaxAndMinPrices(fromDate: String, toDate: String)
                        (implicit csv: Seq[CsvRecord]): Either[Throwable, Json] = {

    val parsedFromDate: Either[WrongDateFormat, LocalDate] = parseRandomDateToLocalDate(fromDate)
    val parsedToDate: Either[WrongDateFormat, LocalDate] = parseRandomDateToLocalDate(toDate)

    (parsedFromDate, parsedToDate) match {
      case (Left(ex), _) =>
        Left(WrongDateFormat(ex.message))
      case (_, Left(ex)) =>
        Left(WrongDateFormat(ex.message))
      case (Right(parsedRightFromDate), Right(parsedRightToDate)) =>
        val csvRecordsFromPeriod: Seq[CsvRecord] =
          getCsvRecordsFromPeriod(csv, parsedRightFromDate, parsedRightToDate)

        if (csvRecordsFromPeriod.isEmpty) {
          Left(NoSuchDateException("Wrong date period"))
        } else
          Right(
            MinAndMaxPrices(
              csvRecordsFromPeriod.minBy(_.averagePrice).averagePrice,
              csvRecordsFromPeriod.maxBy(_.averagePrice).averagePrice
            ).asJson
          )
    }

  }

  /**
   * Gets all csv's lines
   */
  def getStats()(implicit csv: Seq[CsvRecord]): Either[NoSuchDateException, Seq[Json]] = {
    val csvRecordsFromPeriod: Seq[CsvRecord] =
      getCsvRecordsFromPeriod(csv, csv.head.startDate, csv.last.endDate)

    Right(csvRecordsFromPeriod.map(_.asJson))
  }

}
