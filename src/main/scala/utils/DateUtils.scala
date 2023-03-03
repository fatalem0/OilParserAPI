package utils

import exceptions.WrongDateFormat

import java.text.SimpleDateFormat
import java.time.LocalDate
import scala.util.{Failure, Left, Success, Try}

object DateUtils {

  /**
   * Converts the 'month' to its number representing the month
   */
  private def getMonth(month: String): String = {
    val monthsSet = Map(
      "янв" -> "01",
      "фев" -> "02",
      "мар" -> "03",
      "апр" -> "04",
      "май" -> "05",
      "июн" -> "06",
      "июл" -> "07",
      "авг" -> "08",
      "сен" -> "09",
      "окт" -> "10",
      "ноя" -> "11",
      "дек" -> "12"
    )

    monthsSet(month)
  }

  /**
   * Checks if the 'date' is between the 'start' and the 'end'
   */
  def isDateInPeriod(date: LocalDate, start: LocalDate, end: LocalDate): Boolean = {
    start.compareTo(date) <= 0 && end.compareTo(date) >= 0
  }

  /**
   * Converts a date from a csv to a desired format. Let's imagine that the date is correct
   */
  def parseFromCsvToLocalDate(date: String): LocalDate = {
    val formatter = new SimpleDateFormat("yyyy-MM-dd")
    val parser = new SimpleDateFormat("yy-MM-dd")
    val year = date.takeRight(2)
    val month = getMonth(date.substring(3, 6))
    val day = date.take(2)

    LocalDate.parse(formatter.format(parser.parse(s"$year-$month-$day")))
  }

  /**
   * Converts a non-csv-date to a desired format
   */
  def parseRandomDateToLocalDate(date: String): Either[WrongDateFormat, LocalDate] =
    Try(parseFromCsvToLocalDate(date)) match {
      case Success(date) => Right(date)
      case Failure(_) => Left(WrongDateFormat("Wrong date format. It must be in the 'dd.mmm.yy' format"))
    }

}
