package nnmodel.components

import scala.math.{ exp, tanh, pow }

abstract class ActivationFunction(f: Double => Double, val derivative: Double => Double,
  val alternative: Option[Seq[Double] => Seq[Double]] = None) {
  def apply(d: Double) = f(d)
}

object Sigmoid extends ActivationFunction(
  v => 1 / (1 + exp(-v)),
  v => {
    val ev = exp(-v)
    (1 - (1 / (1 + ev))) * (1 / (1 + ev))
  })

object ReLU extends ActivationFunction(
  v => if (v < 0) 0 else v,
  v => if (v < 0) 0 else 1)

object Tanh extends ActivationFunction(
  v => tanh(v),
  v => 1 - pow(tanh(v), 2))

object Identity extends ActivationFunction(
  v => v,
  v => 1)

object Softmax extends ActivationFunction(null, null, Some(seq => {
  val exps = seq.map(exp)
  val expSum = exps.sum
  val z = exps.map(_ / expSum)
  z
}))