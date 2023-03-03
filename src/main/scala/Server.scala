import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import config.Config
import http.HttpRoute

object Server extends App {
  def startApplication(): Unit = {
    implicit val ac: ActorSystem = ActorSystem("oil-parser-api")

    val config = Config.load()

    Http().newServerAt(config.host, config.port).bind(HttpRoute.route)
  }

  startApplication()
}
