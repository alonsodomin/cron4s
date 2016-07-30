package cron4s.matcher

import cron4s.types._

/**
  * Created by alonsodomin on 29/07/2016.
  */
trait MatcherDsl {

  def always[A](value: => Boolean): Matcher[A] = Matcher { _ => value }

  def not[A](m: Matcher[A]): Matcher[A] = Matcher { a => !m(a) }

  def equal[A: Equal](a: A): Matcher[A] = Matcher { b => implicitly[Equal[A]].eqv(a, b) }

  def none[C[_], A](c: C[Matcher[A]])(implicit ev: Foldable[C]): Matcher[A] =
    not(forall(c))

  def exists[C[_], A](c: C[Matcher[A]])(implicit ev: Foldable[C]): Matcher[A] =
    Matcher { a => ev.exists(c)(_(a)) }

  def forall[C[_], A](c: C[Matcher[A]])(implicit ev: Foldable[C]): Matcher[A] =
    Matcher { a => ev.forall(c)(_(a)) }

}
