package http

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import data.CsvRecord
import services.PriceService

class HttpRoute(implicit csv: Seq[CsvRecord]) {

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
                case Left(ex) => HttpEntity(ex.getMessage)
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
                case Left(ex) => HttpEntity(ex.getMessage)
                case Right(value) => HttpEntity(value)
              }
            )
          }
        }
      } ~
      path("getMaxAndMinPrices") {
        get {
          parameters("from".as[String], "to".as[String]) { (fromDate, toDate) =>
            complete(
              PriceService.getMaxAndMinPrices(fromDate, toDate) match {
                case Left(ex) => HttpEntity(ex.getMessage)
                case Right(json) => HttpEntity(ContentTypes.`application/json`, json.toString)
              }
            )
          }
        }
      } ~
        path("getStats") {
          get {
            complete(
              PriceService.getStats match {
                case Left(ex) => HttpEntity(ex.message)
                case Right(json) => HttpEntity(ContentTypes.`application/json`, json.toString)
              }
            )
          }
        }
}
