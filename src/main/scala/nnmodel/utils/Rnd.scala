package nnmodel.utils

import nnmodel.components.{ Bias, Weight }

import scala.math._

object Rnd {
  def gen(mean: Double = 0, sigma: Double = 1): Double = {
    val rand1 = random
    val rand2 = random
    sigma * sqrt(-2 * log(rand1)) * sin(2 * Pi * rand2) + mean
    //    sigma * sqrt(-2 * log(rand1)) * cos(2 * Pi * rand2) + mean
  }
  def weight: Weight = new Weight(gen())
  def bias: Bias = new Bias(gen())
}
