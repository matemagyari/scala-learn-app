package org.bluecollar.akka_http.example


import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import org.scalatest._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class FlowTest extends FunSuite {

  implicit val system = ActorSystem("actor-system")
  implicit val materializer = ActorMaterializer()

  val numSource: Source[Int, Unit] = Source(() => Iterator.range(0, 3))

  test("sources") {

    Source(1 to 4).runForeach(println)

    Source(List(1, 2, 3))

    Source.empty

    Source.single("only one element")

    Source(Future.successful("Hello Streams!"))
  }

//
//  test("sinks") {
//
//    Sink.ignore
//
//    val sink1: Sink[Int, Future[Int]] = Sink.head[Int]
//
//    val foldingSink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)
//
//    val printingSink: Sink[String, Future[Unit]] = Sink.foreach[String](println)
//  }
//
//  test("manipulate sources") {
//    val s1: Source[Int, Unit] = Source(1 to 6).via(Flow[Int].map(_ * 2))
//    val s2: Source[Int, Unit] = Source(1 to 6).map(_ * 2)
//  }
//
//  test("source to sink") {
//    val m: Unit = numSource.runWith(Sink.foreach(println))
//    numSource.runWith(Sink.foreach(println))
//  }
//
//  test("source without explicit sink") {
//    numSource.runForeach(println)
//  }
//
//  test("sources are reusable") {
//    numSource.runForeach(println)
//    numSource.runForeach(println)
//  }
//
//  test("source with ignore sink") {
//    numSource.to(Sink.ignore)
//  }
//
//  test("runnable flow") {
//
//    import scala.concurrent.ExecutionContext.Implicits.global
//
//    val source: Source[Int, Unit] = Source(1 to 3)
//    val sink = Sink.fold[Int, Int](0)(_ + _)
//
//    val counter: RunnableFlow[Future[Int]] = source.toMat(sink)(Keep.right)
//
//    val sum1: Future[Int] = counter.run()
//    val sum2: Future[Int] = counter.run()
//
//    sum1.foreach(println)
//    sum2.foreach(println)
//
//  }
//
//
//  test("flows and materialization") {
//
//    val source = Source(1 to 3)
//    val sink = Sink.fold[Int, Int](0)(_ + _)
//    val flow: Flow[Int, Int, Unit] = Flow[Int].map(_ * 2)
//
//    val runnableFlow: RunnableFlow[Unit] = source.via(flow).to(sink)
//    val result1: Future[Int] = source.via(flow).runWith(sink)
//    val runnableFlow2: RunnableFlow[Future[Int]] = source.toMat(sink)(Keep.right)
//    val result2: Future[Int] = runnableFlow2.run()
//    val runWith: Unit = flow.to(sink).runWith(source)
//    val x: (Unit, Future[Int]) = flow.runWith(source, sink)
//
//  }
//
//  test("simple graph with 1 source and 1 sink") {
//
//    val sumSink = Sink.fold[Int, Int](0)(_ + _)
//
//    val graph: RunnableFlow[Future[Int]] = FlowGraph.closed(sumSink) { implicit b: FlowGraph.Builder[Future[Int]] =>
//
//      sink: Sink[Int, Future[Int]]#Shape =>
//
//        import akka.stream.scaladsl.FlowGraph.Implicits._
//
//        Source(1 to 3) ~> Flow[Int].map(_ * 2) ~> sink.inlet
//    }
//
//    val fr: Future[Int] = graph.run()
//
//    val result: Int = Await.result(fr, Duration.create(100, TimeUnit.MILLISECONDS))
//
//    assert(result == 12)
//  }
//
//  test("simple graph with 1 source and 2 sinks") {
//
//    val sink1: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)
//    val sink2: Sink[Int, Future[Int]] = Sink.fold[Int, Int](1)(_ * _)
//
//    val graph: RunnableFlow[(Future[Int], Future[Int])] = FlowGraph.closed(sink1, sink2)((_, _)) { implicit b =>
//      (s1, s2) =>
//
//        import akka.stream.scaladsl.FlowGraph.Implicits._
//
//        val bcast = b.add(Broadcast[Int](2))
//
//        Source(1 to 3) ~> bcast.in
//        bcast.out(0) ~> Flow[Int].map(_ + 1) ~> s1.inlet
//        bcast.out(1) ~> Flow[Int].map(_ * 2) ~> s2.inlet
//    }
//
//    val (r1, r2): (Future[Int], Future[Int]) = graph.run()
//
//    assert(9 == Await.result(r1, Duration.create(100, TimeUnit.MILLISECONDS)))
//    assert(48 == Await.result(r2, Duration.create(100, TimeUnit.MILLISECONDS)))
//
//  }
//
//  /* */
//  test("car factory") {
//
//    trait Part {
//      def faulty: Boolean
//    }
//    case class Engine(faulty: Boolean) extends Part
//    case class Wheel(faulty: Boolean) extends Part
//    case class Coachwork(faulty: Boolean) extends Part
//
//    sealed abstract class Color
//    object Blue extends Color
//    object Green extends Color
//    object Red extends Color
//    object None extends Color
//
//    case class Car(engine: Engine,
//                   coachwork: Coachwork,
//                   wheels: Seq[Wheel],
//                   color: Color) {
//
//      if (wheels.length != 4) {
//        throw new RuntimeException(s"Wrong number of wheels: ${wheels.length}")
//      }
//    }
//
//    val engineSource = Source(() => Iterator.continually(Engine(false)))
//    val wheelSource = Source(() => Iterator.continually(Wheel(false)))
//    val coachworkSource = Source(() => Iterator.continually(Coachwork(false)))
//
//    def filterFor[T <: Part] = Flow[T].filter(!_.faulty)
//    def createPainter(color: Color) = Flow[Car].map(c => c.copy(color = color))
//    val bluePainter = createPainter(Blue)
//    val greenPainter = createPainter(Green)
//    val redPainter = createPainter(Red)
//
//    val sumSink = Sink.fold[Int, Car](0)((sum: Int, c: Car) => sum + 1)
//
//    val graph: RunnableFlow[Future[Int]] = FlowGraph.closed(sumSink) { implicit b: FlowGraph.Builder[Future[Int]] =>
//
//      s =>
//
//        val carAssembler = b.add(ZipWith[Engine, Coachwork, Seq[Wheel], Car] {
//          (e, c, w) => new Car(e, c, w, None)
//        })
//
//        val splitter = b.add(Balance[Car](3))
//
//        val merge = b.add(Merge[Car](3))
//
//        import akka.stream.scaladsl.FlowGraph.Implicits._
//
//        engineSource ~> filterFor[Engine] ~> carAssembler.in0
//        coachworkSource ~> filterFor[Coachwork] ~> carAssembler.in1
//        wheelSource ~> filterFor[Wheel].grouped(4) ~> carAssembler.in2
//
//        carAssembler.out ~> splitter.in
//
//        splitter.out(0) ~> bluePainter ~> merge
//        splitter.out(1) ~> redPainter ~> merge
//        splitter.out(2) ~> greenPainter ~> merge
//
//        merge.out.take(1000 * 1000) ~> s.inlet
//    }
//
//    val start = System.currentTimeMillis()
//    val fr: Future[Int] = graph.run()
//
//    val result: Int = Await.result(fr, Duration.create(10000, TimeUnit.MILLISECONDS))
//
//    val end = System.currentTimeMillis()
//
//    println(s"Duration: ${end - start}")
//
//    assert(result == 1000 * 1000)
//  }

}
