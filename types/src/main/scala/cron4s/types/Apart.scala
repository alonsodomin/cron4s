package cron4s.types

/**
  * Created by domingueza on 26/08/2016.
  */
trait Apart[F] {
  type T
  type W[X]

  def apply(f: F): W[T]
}
object Apart {
  def apply[F](implicit ev: Apart[F]) = ev

  type Aux[FA, A, F[_]] = Apart[FA] { type T = A; type W[X] = F[X] }

  implicit def mk[F[_], A]: Aux[F[A], A, F] = new Apart[F[A]] {
    type T = A
    type W[X] = F[X]

    def apply(f: F[A]): W[T] = f
  }
}
