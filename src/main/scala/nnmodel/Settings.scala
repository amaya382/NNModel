package nnmodel

import nnmodel.components.{ Bias, Weight }
import nnmodel.utils.Rnd

case class Settings(
  weight: () => Weight = () => Rnd.weight,
  bias: () => Bias = () => Bias(0),
  var learningRate: Double = 0.05)
