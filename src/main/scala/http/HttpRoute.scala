package http

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import services.ParsingService.readFromCsvFile
import services.PriceService

object HttpRoute {

  implicit val csv: Seq[PriceService.CsvRecord] = readFromCsvFile(getClass.getResource("/data.csv"))

  val route: Route =
    path("") {
      get {
        complete(
          HttpEntity("Server is working now!")
        )
      }
    } ~
      path("getPriceByDate") {
        get {
          parameters("date".as[String]) { date =>
            complete(
              PriceService.getPriceByDate(date) match {
                case Left(ex) => HttpEntity(ex.message)
                case Right(value) => HttpEntity(value)
              }
            )
          }
        }
      } ~
      path("getAvgPriceByPeriod") {
        get {
          parameters("from".as[String], "to".as[String]) { (fromDate, toDate) =>
            complete(
              PriceService.getAvgPriceByPeriod(fromDate, toDate) match {
                case Left(ex) => HttpEntity(ex.message)
                case Right(value) => HttpEntity(value)
              }
            )
          }
        }
      }

}
