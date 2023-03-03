package services

import data.CsvRecord
import kantan.csv.ops.toCsvInputOps
import kantan.csv.{CsvReader, HeaderDecoder, ReadResult, rfc}
import utils.DateUtils.parseFromCsvToLocalDate

import java.net.URL

object ParsingService {

  case class ParsedLine(startDate: String, endDate: String, averagePrice: String)

  /**
   * Reads .csv at the given 'path' and converts each line (except for the header) to the 'CsvRecord'
   */
  def readFromCsvFile(path: URL): Seq[CsvRecord] = {
    implicit val carDecoder: HeaderDecoder[ParsedLine] =
      HeaderDecoder.decoder(
        "Начало периодамониторинга цен на нефть",
        "Конец периодамониторинга цен на нефть",
        "Средняя цена на нефть сырую марки «Юралс» на мировых рынках нефтяного сырья (средиземноморском и роттердамском)"
      )(ParsedLine.apply)

    val parsedCsv: CsvReader[ReadResult[ParsedLine]] =
      path.asCsvReader[ParsedLine](rfc.withHeader)

    parsedCsv.collect {
      case Right(line) =>
        CsvRecord(
          parseFromCsvToLocalDate(line.startDate),
          parseFromCsvToLocalDate(line.endDate),
          line.averagePrice.replace(',', '.')
        )
    }.toSeq
  }

}
