package nnmodel.components

case class Neuron(
  bias: Bias,
  af: ActivationFunction,
  var delta: Double = 0,
  var u: Double = 0,
  var z: Double = 0) {
  def ->:(input: Double) = {
    u = input + bias.b
    z = af(u)
    z
  }

  def biasing(d: Double): Double = {
    u = bias.b + d
    u
  }
}

