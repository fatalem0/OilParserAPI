package data

import io.circe.Encoder

import java.time.LocalDate

case class CsvRecord(startDate: LocalDate, endDate: LocalDate, averagePrice: String)

object CsvRecord {
  implicit val encodeCsvRecord: Encoder[CsvRecord] =
    Encoder.forProduct3("start_date", "end_date", nameA2 = "average_price")(csvRecord =>
      (csvRecord.startDate, csvRecord.endDate, csvRecord.averagePrice)
    )
}