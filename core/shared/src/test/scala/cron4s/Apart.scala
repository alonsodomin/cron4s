package cron4s

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait Apart[A] {
  type Inner
  type Outer[X]

  def apply(a: A): Outer[Inner]
}

object Apart {
  def apply[A](implicit ev: Apart[A]) = ev

  type Aux[AB, B, A[_]] = Apart[AB] { type Inner = B; type Outer[X] = A[X] }

  implicit def mk[A[_], B]: Aux[A[B], B, A] = new Apart[A[B]] {
    type Inner = B
    type Outer[X] = A[X]

    def apply(a: A[B]): Outer[Inner] = a
  }
}
