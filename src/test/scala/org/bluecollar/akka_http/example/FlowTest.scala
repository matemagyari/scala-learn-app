package org.bluecollar.akka_http.example

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import org.scalatest._

import scala.concurrent.forkjoin.ThreadLocalRandom

class FlowTest extends FunSuite {

  test("something") {

    implicit val system = ActorSystem("primes")
    implicit val materializer = ActorFlowMaterializer()

    val primeSource: Source[Int, Unit] =
      Source(() => Iterator.continually(ThreadLocalRandom.current().nextInt(100000)))
        .filter(_ % 2 == 0)

    //val sumSink = Sink.fold(0)(_+_)

    //val materialized = FlowGraph.closed(sumSink)

    //primeSource.runWith(Sink.foreach(println))

    assert(true)
  }
}
