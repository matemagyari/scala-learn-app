package org.bluecollar.akka_http.example


import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Try, Failure, Success}

class FutureTest extends FunSuite {

  import scala.concurrent.ExecutionContext.Implicits.global

  test("simple future") {

    val f: Future[Int] = Future { 1 }

    assert(Await.result(f, 100 milliseconds) == 1)
  }

  test("futures are monads (or something)") {

    val f: Future[Int] = Future { 1 }
    val f2 = f.map(_ * 2)

    assert(Await.result(f2, 100 milliseconds) == 2)
  }


  test("futures compose") {

    val f1 = Future { 1 }
    val f2 = Future { 2 }

    val f3 = for {
      x1 <- f1
      x2 <- f2
    } yield x1 + x2

    assert(Await.result(f3, 100 milliseconds) == 3)
  }


  test("future sequence") {

    val fs: IndexedSeq[Future[Int]] = (1 to 3).map(i => Future { i })

    val f: Future[IndexedSeq[Int]] = Future.sequence(fs)

    assert(Await.result(f, 100 milliseconds) == IndexedSeq(1, 2, 3))
  }

  test("future firstCompletedOf") {

    val f1 = Future {
      Thread.sleep(500); 1
    }
    val f2 = Future {
      Thread.sleep(100); 2
    }

    val f = Future.firstCompletedOf(List(f1, f2))

    assert(Await.result(f, 100 milliseconds) == 2)
  }

  test("future callbacks") {

    val f = Future { 1 }

    f.onSuccess {
      case s => println(s"Success1: ${s}")
    }
    f.onFailure {
      case e => println(s"Failure1: ${e}")
    }
    f.onComplete {
      case Success(s) => println(s"Success2: ${s}")
      case Failure(e) => println(s"Failure2: ${e}")
    }
  }


  test("future andThen-s") {

    val f: Future[Int] = Future {
      1
    } andThen {
      case Success(s) => println(s"Success ${s}")
      case Failure(t) => throw new Exception
    }

    assert(Await.result(f, 100 milliseconds) == 1)
  }


  test("future andThen-s 2") {

    val f = Future {
      1 / 0
    } andThen {
      case Success(s) => println(s"Success ${s}")
      case Failure(t) => println(s"Failure ${t}"); 0
    }

    val r = Try { assert(Await.result(f, 100 milliseconds) == 1) }

    assert(r.isFailure)
  }


  test("fallbackTo") {

    val f = Future { 1 / 0 } fallbackTo Future { 2 }

    assert(Await.result(f, 100 milliseconds) == 2)
  }


  test("recover") {

    val f = Future { 1 / 0 } recover {
      case e:ArithmeticException => 2
    }

    assert(Await.result(f, 100 milliseconds) == 2)
  }

  test("recoverWith") {

    val f = Future { 1 / 0 } recoverWith {
      case e:ArithmeticException => Future { 2 }
    }

    assert(Await.result(f, 100 milliseconds) == 2)
  }

}
