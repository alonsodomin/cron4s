package cron4s.types.std.vector

import cron4s.types.{Applicative, Foldable, Traverse}

/**
  * Created by alonsodomin on 29/07/2016.
  */
private[std] trait VectorInstances {

  implicit val vectorInstance = new Traverse[Vector] {
    override def foldLeft[A, B](fa: Vector[A], b: B)(f: (B, A) => B): B =
      fa.foldLeft(b)(f)

    override def foldRight[A, B](fa: Vector[A], b: => B)(f: (A, => B) => B): B =
      fa.foldRight(b)((a, b) => f(a, b))

    override def traverse[G[_], A, B](fa: Vector[A])(f: A => G[B])(implicit G: Applicative[G]): G[Vector[B]] =
      foldLeft[A, G[Vector[B]]](fa, G.pure(Vector.empty[B])) { (acc, a) =>
        G.map(G.product(f(a), acc)) { case (b, vec) => vec :+ b }
      }

    override def map[A, B](fa: Vector[A])(f: A => B): Vector[B] = fa.map(f)
  }

}
