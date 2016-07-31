package cron4s.types

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait Traverse[F[_]] extends Functor[F] with Foldable[F] {

  def traverse[G[_]: Applicative, A, B](fa: F[A])(f: A => G[B]): G[F[B]]

  def sequence[G[_]: Applicative, A](fga: F[G[A]]): G[F[A]] =
    traverse(fga)(identity)

}

object Traverse {
  @inline def apply[F[_]](implicit ev: Traverse[F]): Traverse[F] = ev
}
