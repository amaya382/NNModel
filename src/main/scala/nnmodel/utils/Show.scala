package nnmodel.utils

object Show {
  def asAA(seq: Seq[Double]) = {
    seq.zipWithIndex.foreach(p => {
      if (p._2 % 28 == 0)
        println()

      if (p._1 == 0)
        print("□")
      else
        print("■")
    })
    println()
  }
}
