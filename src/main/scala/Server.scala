import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import config.Config
import data.CsvRecord
import http.HttpRoute
import services.ParsingService.readFromCsvFile

object Server extends App {
  private def startApplication(): Unit = {
    val config = Config.load()

    implicit val ac: ActorSystem = ActorSystem("oil-parser-api")
    implicit val csv: Seq[CsvRecord] = readFromCsvFile(getClass.getResource(s"/${config.csv}"))

    Http().newServerAt(config.host, config.port).bind(new HttpRoute().route)
  }

  startApplication()
}
