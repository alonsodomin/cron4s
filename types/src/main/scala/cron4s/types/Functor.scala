package cron4s.types

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait Functor[F[_]] extends Any with Serializable {

  def map[A, B](fa: F[A])(f: A => B): F[B]

}

object Functor {
  @inline def apply[F[_]](implicit ev: Functor[F]): Functor[F] = ev
}
