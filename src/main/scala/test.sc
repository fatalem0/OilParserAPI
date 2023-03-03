import java.io.File
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

case class ParsedLine(startDate: String, endDate: String, averagePrice: String)

def readFromCsvFile(path: String) = {

  val src = Source.fromFile(path)
  val lines = src.getLines().mkString

  src.close()

  lines
}

val csv =
  readFromCsvFile(
    "/Users/fatalem0/IdeaProjects/OilParserAPI/src/main/resources/data.csv"
  )

import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
//import kantan.csv.java8._

//implicit val headerDecoder =
//  HeaderDecoder.decoder(
//    "Начало периодамониторинга цен на нефть", "Конец периодамониторинга цен на нефть",
//    "Средняя цена на нефть сырую марки «Юралс» на мировых рынках нефтяного сырья (средиземноморском и роттердамском)"
//  )


//val reader = csv.asCsvReader[CsvRecord](rfc.withHeader)

implicit val carDecoder: HeaderDecoder[ParsedLine] =
  HeaderDecoder.decoder(
    "Начало периодамониторинга цен на нефть",
    "Конец периодамониторинга цен на нефть",
    "Средняя цена на нефть сырую марки «Юралс» на мировых рынках нефтяного сырья (средиземноморском и роттердамском)"
  )(ParsedLine.apply)

val test =
  new File(
    "/Users/fatalem0/IdeaProjects/OilParserAPI/src/main/resources/data.csv"
  ).asCsvReader[ParsedLine](rfc.withHeader)

case class CsvRecord(startDate: String, endDate: String, averagePrice: String)

val test1 = test.collect {
  case Right(v) => CsvRecord(v.startDate, v.endDate, v.averagePrice)
}