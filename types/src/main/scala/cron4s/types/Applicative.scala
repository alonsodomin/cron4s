package cron4s.types

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait Applicative[F[_]] extends Functor[F] {

  def pure[A](a: A): F[A]

  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]

  def product[A, B](fa: F[A], fb: F[B]): F[(A, B)] =
    ap(map(fa)(a => (b: B) => (a, b)))(fb)

}

object Applicative {
  @inline def apply[F[_]](implicit ev: Applicative[F]): Applicative[F] = ev
}
