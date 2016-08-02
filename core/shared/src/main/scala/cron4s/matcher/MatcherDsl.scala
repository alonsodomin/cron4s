package cron4s.matcher

import scalaz.{Equal, Foldable}

/**
  * Created by alonsodomin on 29/07/2016.
  */
trait MatcherDsl {

  def always[A](value: => Boolean): Matcher[A] = Matcher { _ => value }

  def not[A](m: Matcher[A]): Matcher[A] = Matcher { a => !m(a) }

  def equal[A: Equal](a: A): Matcher[A] = Matcher { b => implicitly[Equal[A]].equal(a, b) }

  def none[C[_], A](c: C[Matcher[A]])(implicit ev: Foldable[C]): Matcher[A] =
    not(allOf(c))

  def anyOf[C[_], A](c: C[Matcher[A]])(implicit ev: Foldable[C]): Matcher[A] =
    Matcher { a => ev.any(c)(_(a)) }

  def allOf[C[_], A](c: C[Matcher[A]])(implicit ev: Foldable[C]): Matcher[A] =
    Matcher { a => ev.all(c)(_(a)) }

}
