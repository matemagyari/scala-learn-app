package org.bluecollar.akka_http.example

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorFlowMaterializer

object WebServer extends App {

  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorFlowMaterializer()

  val logger = Logging(system, getClass)

  val routes = {
    get {
      path("") {
        getFromResource("web/index.html")
      }
    }
  }

  Http().bindAndHandle(handler = routes, interface = "localhost", port = 8080)


}
