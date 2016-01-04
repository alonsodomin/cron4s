package cron4s.matcher

import cats.functor.Contravariant

/**
  * Created by alonsodomin on 02/01/2016.
  */
trait Matcher[A] extends (A => Boolean)

object Matcher {

  def apply[A](f: A => Boolean): Matcher[A] = new Matcher[A] {
    def apply(a: A): Boolean = f(a)
  }

  implicit val matcherCofunctor = new Contravariant[Matcher] {

    override def contramap[A, B](fa: Matcher[A])(f: B => A): Matcher[B] = Matcher { b => fa(f(b)) }

  }

}
