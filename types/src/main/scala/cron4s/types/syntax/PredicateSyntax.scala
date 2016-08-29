package cron4s.types.syntax

import cron4s.types.Predicate

import scalaz.{Equal, Foldable}

/**
  * Created by alonsodomin on 29/07/2016.
  */
trait PredicateSyntax {

  def always[A](value: => Boolean): Predicate[A] = Predicate { _ => value }

  def not[A](m: Predicate[A]): Predicate[A] = Predicate { a => !m(a) }

  @deprecated("Use equalTo instead", "0.2.0")
  def equal[A: Equal](a: A): Predicate[A] = equalTo[A](a)
  def equalTo[A: Equal](a: A): Predicate[A] = Predicate { b => implicitly[Equal[A]].equal(a, b) }

  def none[C[_], A](c: C[Predicate[A]])(implicit ev: Foldable[C]): Predicate[A] =
    not(allOf(c))

  def anyOf[C[_], A](c: C[Predicate[A]])(implicit ev: Foldable[C]): Predicate[A] =
    Predicate { a => ev.any(c)(_(a)) }

  def allOf[C[_], A](c: C[Predicate[A]])(implicit ev: Foldable[C]): Predicate[A] =
    Predicate { a => ev.all(c)(_(a)) }

}
