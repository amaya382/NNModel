package nnmodel.components

import nnmodel.Settings
import nnmodel.utils.{ Matrix, MatrixBase }
import nnmodel.utils.PimpMyLibrary.RichSeq
import nnmodel.utils.PimpMyLibrary.RichSeqSeq

case class Network(layers: Seq[Seq[Neuron]], arrows: Seq[MatrixBase[Weight]], settings: Settings) {
  require(layers.length == arrows.length)

  override def toString = {
    val sb = new StringBuilder
    layers.zipWithIndex.foreach(p => {
      sb.append(s"layer${p._2}: ")
      p._1.foreach(e => sb.append(e.bias.b.toString + ","))
      sb.append("\n")
    })
    sb.toString
  }

  def hiddenLayer(nUnits: Int, af: ActivationFunction = Sigmoid) =
    Network(
      layers :+ Seq.fill(nUnits)(Neuron(settings.bias(), af)),
      arrows :+ Matrix(IndexedSeq.fill(layers.last.length * nUnits)(settings.weight()), nUnits), settings)

  def outputLayer(nUnits: Int, af: ActivationFunction = Softmax) =
    Network(
      layers :+ Seq.fill(nUnits)(Neuron(settings.bias(), af)),
      arrows :+ Matrix(IndexedSeq.fill(layers.last.length * nUnits)(settings.weight()), nUnits), settings)

  def run(input: Seq[Double]) = {
    @annotation.tailrec
    def go(d: Seq[(Seq[Neuron], MatrixBase[Weight])], _input: Seq[Double]): Seq[Double] =
      d match {
        case Nil =>
          _input
        case h :: tail =>
          val a = h._2.zipMap(_input)((row, n) => row.map(n * _.w)) //input * weight
            .toMatrix.t

          go(tail,
            //aggregation toward each target unit -> biasing -> af transformation
            //switching by type of af
            h._1.head.af.alternative match {
              case Some(f) =>
                val us = f(a.zipMap(h._1)((row, n) => n.biasing(row.sum)))
                h._1.zip(us).foreach(p => p._1.z = p._2)
                us
              case None =>
                a.zipMap(h._1)((row, n) => row.sum ->: n)
            })
      }
    layers.head.zip(input).foreach(p => p._2 ->: p._1) //set up input layer
    go(layers.zip(arrows).drop(1).toList, input)
  }

  def train(input: Seq[Double], expected: Seq[Double]) = {
    val sreyal = layers.reverse
    val sworra = arrows.reverse

    //calc deltas of output layer
    run(input).zip(expected).map(p => p._1 - p._2) //calc
      .zip(sreyal.head).foreach(p => p._2.delta = p._1) //set

    //backpropagation
    sreyal.zip(sworra).slidingWindow(win => {
      val m = win.head._2
      val lower = win.head._1
      val upper = win.last._1

      m.zipMap(upper)((row, n) => {
        n.delta = row.zip(lower).map(p => {
          val w = p._2.delta * p._1.w * n.af.derivative(n.u)
          p._1.w = p._1.w - settings.learningRate * p._2.delta * n.z
          w
        }).sum
      })
      //↑ == ↓
      /*
      //propagate deltas
      m.zipMap(upper)((row, n) =>
        n.delta = row.zip(lower).map(p => p._2.delta * p._1.w * n.af.derivative(n.u)).sum)

      //update weight
      m.zipMap(upper)((row, n) => {
        row.zip(lower).foreach(p => p._1.w = p._1.w - settings.learningRate * p._2.delta * n.z)
      })
      */

      //update bias
      lower.foreach(n => n.bias.b = n.bias.b - settings.learningRate * n.delta)
    }, 2)
  }
}

object Network {
  def inputLayer(nInputs: Int, settings: Settings = Settings()) =
    Network(
      Array(Seq.fill(nInputs)(Neuron(Bias(0), Identity))),
      Array(Matrix(IndexedSeq.fill(1 * nInputs)(Weight(1)), nInputs)), settings)
}