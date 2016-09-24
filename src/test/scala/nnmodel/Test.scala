package nnmodel

import nnmodel.components.Network
import nnmodel.utils.{ Matrix, Show }
import nnmodel.utils.PimpMyLibrary.RichSeq
import org.scalacheck.{ Gen, Properties }
import org.scalacheck.Prop.forAll

import scala.io.Source

object SlidingWindowTest extends Properties("SlidingWindow") {
  property("slidingWindow") = forAll(Gen.choose(3, 100), Gen.choose(1, 2)) { (i, j) =>
    val seq = Seq.fill(i)(1)
    seq.slidingWindow(sub => sub.sum, j).sum == (seq.length - j + 1) * j
  }
}

object MatrixTest extends Properties("Matrix") {
  val one2Ten = Gen.choose(1, 10)

  property("transpose") = forAll(one2Ten, one2Ten) { (i, j) =>
    Matrix(0 until i * j, i).data == Matrix(0 until j * i, j).t.data
  }

  property("transpose.transpose") = forAll(one2Ten, one2Ten) { (i, j) =>
    val m0 = Matrix(0 until i * j, i)
    val m1 = m0.t
    val m2 = m1.t
    m0.nRows == m2.nRows && m0.nCols == m2.nCols && m0.data == m2.data
  }

  property("map") = forAll(one2Ten, one2Ten) { (i, j) =>
    val m = Matrix(0 until i * j, i).map(identity)
    m.length == j
  }

  property("transpose.map") = forAll(one2Ten, one2Ten) { (i, j) =>
    val m = Matrix(0 until i * j, i).t.map(identity)
    m.length == i
  }

  property("zipMap") = forAll(one2Ten, one2Ten, one2Ten) { (i, j, k) =>
    val m = Matrix(0 until i * j, i)
    val seq = 0 until k
    val zipped = m.zipMap(seq)((s, u) => s.map(_ + u))
    zipped.length == scala.math.min(j, k)
  }
}

object RandomTest extends Properties("Random") {
  val one2Ten = Gen.choose(1, 10)

  property("run") = forAll(one2Ten, one2Ten, one2Ten) { (i, j, k) =>
    val nn = Network
      .inputLayer(i)
      .hiddenLayer(j)
      .outputLayer(k)
    val result = nn.run(Seq.fill(i)(1.0))
    result.length == k
  }

  property("train") = forAll(
    one2Ten, one2Ten, one2Ten, one2Ten) { (i, j, k, l) =>
      val nn = Network
        .inputLayer(i)
        .hiddenLayer(j)
        .hiddenLayer(k)
        .outputLayer(l)

      nn.train(Seq.fill(i)(scala.util.Random.nextDouble), Seq.fill(l)(1))
      true
    }
}

object PracticalTest extends Properties("Practical") {
  val one2Ten = Gen.choose(1, 10)
  val data = {
    val rawData = Source.fromFile("train.csv").getLines().drop(1).takeWhile(_ != "").take(5000).toList
    rawData.map(r => {
      val arr = r.split(",").map(_.toDouble)
      val buf = scala.collection.mutable.ArrayBuffer.fill(10)(0.0)
      buf(arr.head.toInt) = 1.0
      buf.toList -> arr.tail.map(_ / 255) //len == 784
    })
  }

  org.scalacheck.Test.check({
    val nn = Network
      .inputLayer(784)
      .hiddenLayer(100)
      .outputLayer(10)

    val test = () =>
      data.take(10).foreach(d => {
        val expected = d._1.indexWhere(_ == 1)
        val calculated = nn.run(d._2)
//        Show.asAA(d._2)
        println(s"expected: $expected, result: $calculated")
      })

    test()
    data.drop(10).foreach(d => nn.train(d._2, d._1))
    test()

    true
  })(identity)
}
