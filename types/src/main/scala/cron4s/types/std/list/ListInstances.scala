package cron4s.types.std.list

import cron4s.types.{Applicative, Traverse}

/**
  * Created by alonsodomin on 31/07/2016.
  */
private[std] trait ListInstances {

  implicit val listInstance = new Traverse[List] {
    override def traverse[G[_], A, B](fa: List[A])(f: (A) => G[B])(implicit G: Applicative[G]): G[List[B]] = {
      foldRight[A, G[List[B]]](fa, G.pure(List.empty[B])) { (a, acc) =>
        G.map(G.product(f(a), acc)) { case (b, list) => b :: list }
      }
    }

    override def foldLeft[A, B](fa: List[A], b: B)(f: (B, A) => B): B =
      fa.foldLeft(b)(f)

    override def foldRight[A, B](fa: List[A], b: => B)(f: (A, => B) => B): B =
      fa.foldRight(b)((a, b) => f(a, b))

    override def map[A, B](fa: List[A])(f: (A) => B): List[B] = fa.map(f)
  }

}
