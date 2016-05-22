package nnmodel.utils

object PimpMyLibrary {
  implicit class RichSeq[T](val seq: Seq[T]) {
    def slidingWindow[U](executor: Seq[T] => U, windowSize: Int, slidingSize: Int = 1): Seq[U] = {
      assert(seq.length >= windowSize)

      for {
        i <- 0 to seq.length - windowSize by slidingSize
      } yield {
        executor(seq.slice(i, i + windowSize))
      }
    }
  }

  implicit class RichSeqSeq[T](val seq: Seq[Seq[T]]) {
    def toMatrix: MatrixBase[T] = {
      Matrix(seq.flatten.toIndexedSeq, seq.head.length)
    }
  }
}
