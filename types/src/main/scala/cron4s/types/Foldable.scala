package cron4s.types

/**
  * Created by alonsodomin on 29/07/2016.
  */
trait Foldable[F[_]] extends Any with Serializable {

  def foldLeft[A, B](fa: F[A], b: B)(f: (B, A) => B): B

  def foldRight[A, B](fa: F[A], b: => B)(f: (A, => B) => B): B

  def exists[A](fa: F[A])(p: A => Boolean): Boolean =
    foldRight(fa, false) { (a, b) =>
      if (p(a)) true
      else b
    }

  def forall[A](fa: F[A])(p: A => Boolean): Boolean =
    foldRight(fa, true) { (a, b) =>
      if (p(a)) b
      else false
    }

}

object Foldable {
  @inline def apply[F[_]](implicit F: Foldable[F]): Foldable[F] = F
}
