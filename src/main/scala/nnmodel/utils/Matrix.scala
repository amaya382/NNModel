package nnmodel.utils

import PimpMyLibrary.RichSeq

abstract class MatrixBase[T](val data: IndexedSeq[T], val dim: Int, transpose: Boolean = false) {
  val nCols = dim
  val nRows = data.length / dim

  def map[U](f: Seq[T] => U): Seq[U]

  def zip[U](seq: Seq[U]): Seq[(Seq[T], U)] =
    zipMap(seq)(_ -> _)

  def zipMap[U, V](seq: Seq[U])(f: (Seq[T], U) => V): Seq[V]

  def t = {
    new MatrixT(data, nRows)
  }
}

class MatrixN[T](override val data: IndexedSeq[T], override val dim: Int) extends MatrixBase[T](data, dim) {
  override def map[U](f: Seq[T] => U): Seq[U] = {
    data.slidingWindow(f, nCols, nCols)
  }

  override def zipMap[U, V](seq: Seq[U])(f: (Seq[T], U) => V): Seq[V] = {
    val len = scala.math.min(nRows, seq.length)
    for {
      i <- 0 until len
    } yield {
      f(data.slice(i * nCols, i * nCols + nCols), seq(i))
    }
  }
}

class MatrixT[T](override val data: IndexedSeq[T], override val dim: Int) extends MatrixBase[T](data, dim) {
  override def map[U](f: Seq[T] => U): Seq[U] = {
    (for {
      i <- 0 until nRows
      j <- 0 until nCols
    } yield {
      data(i + j * nRows)
    }).slidingWindow(f, nCols, nCols)
  }

  override def zipMap[U, V](seq: Seq[U])(f: (Seq[T], U) => V): Seq[V] = {
    map(identity).zip(seq).map(p => f(p._1, p._2)) //TODO
  }
}

object Matrix {
  def apply[T](data: IndexedSeq[T], dim: Int, transpose: Boolean = false) = {
    if (!transpose)
      new MatrixN(data, dim)
    else
      new MatrixT(data, dim)
  }
}
