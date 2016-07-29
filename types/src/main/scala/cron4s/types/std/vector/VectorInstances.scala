package cron4s.types.std.vector

import cron4s.types.Foldable

/**
  * Created by alonsodomin on 29/07/2016.
  */
private[std] trait VectorInstances {

  implicit val vectorInstance = new Foldable[Vector] {
    override def foldLeft[A, B](fa: Vector[A], b: B)(f: (B, A) => B): B =
      fa.foldLeft(b)(f)

    override def foldRight[A, B](fa: Vector[A], b: => B)(f: (A, => B) => B): B =
      fa.foldRight(b)((a, b) => f(a, b))

  }

}
