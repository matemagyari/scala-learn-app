package org.bluecollar.akka_http.example

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import org.scalatest._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class FlowTest extends FunSuite {

  implicit val system = ActorSystem("actor-system")
  implicit val materializer = ActorFlowMaterializer()

  val numSource: Source[Int, Unit] = Source(() => Iterator.range(0, 3))

  test("sources") {

    Source(1 to 4).runForeach(println)

    Source(List(1, 2, 3))

    Source.empty

    Source.single("only one element")

    Source(Future.successful("Hello Streams!"))
  }

  
  test("sinks") {

    Sink.ignore

    val sink1: Sink[Int, Future[Int]] = Sink.head[Int]

    val foldingSink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

    val printingSink: Sink[String, Future[Unit]] = Sink.foreach[String](println)
  }

  test("manipulate sources") {
    val s1: Source[Int, Unit] = Source(1 to 6).via(Flow[Int].map(_ * 2))
    val s2: Source[Int, Unit] = Source(1 to 6).map(_ * 2)
  }

  test("source to sink") {
    val m: Unit = numSource.runWith(Sink.foreach(println))
    numSource.runWith(Sink.foreach(println))
  }

  test("source without explicit sink") {
    numSource.runForeach(println)
  }

  test("sources are reusable") {
    numSource.runForeach(println)
    numSource.runForeach(println)
  }

  test("source with ignore sink") {
    numSource.to(Sink.ignore)
  }

  test("runnable flow") {

    import scala.concurrent.ExecutionContext.Implicits.global

    val source: Source[Int, Unit] = Source(1 to 3)
    val sink = Sink.fold[Int, Int](0)(_ + _)

    val counter: RunnableFlow[Future[Int]] = source.toMat(sink)(Keep.right)

    val sum1: Future[Int] = counter.run()
    val sum2: Future[Int] = counter.run()

    sum1.foreach(println)
    sum2.foreach(println)

  }


  test("flows and materialization") {

    val source = Source(1 to 3)
    val sink = Sink.fold[Int, Int](0)(_ + _)
    val flow: Flow[Int, Int, Unit] = Flow[Int].map(_ * 2)

    val runnableFlow: RunnableFlow[Unit] = source.via(flow).to(sink)
    val result1: Future[Int] = source.via(flow).runWith(sink)
    val runnableFlow2: RunnableFlow[Future[Int]] = source.toMat(sink)(Keep.right)
    val result2: Future[Int] = runnableFlow2.run()
    val runWith: Unit = flow.to(sink).runWith(source)
    val x: (Unit, Future[Int]) = flow.runWith(source, sink)

  }

  test("simple graph") {

    val sumSink = Sink.fold[Int, Int](0)(_ + _)

    val graph: RunnableFlow[Future[Int]] = FlowGraph.closed(sumSink) { implicit b: FlowGraph.Builder[Future[Int]] =>

      sink: Sink[Int, Future[Int]]#Shape =>

        import akka.stream.scaladsl.FlowGraph.Implicits._

        Source(1 to 3) ~> Flow[Int].map(_ * 2) ~> sink.inlet
    }

    val fr: Future[Int] = graph.run()

    val result: Int = Await.result(fr, Duration.create(100, TimeUnit.MILLISECONDS))

    assert(result == 12)
  }

  test("broadcast") {

    val sink1: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)
    val sink2: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ * _)

    val graph: RunnableFlow[Unit] = FlowGraph.closed() { implicit builder: FlowGraph.Builder[Unit] =>
      import akka.stream.scaladsl.FlowGraph.Implicits._

      val bcast = builder.add(Broadcast[Int](2))

      Source(1 to 3) ~> bcast.in
      bcast.out(0) ~> Flow[Int].map(_ + 1) ~> sink1
      bcast.out(1) ~> Flow[Int].map(_ * 2) ~> sink2
    }

    // graph.run()


  }
}
