package cron4s.types

import cron4s.syntax.predicate._

import scalaz.{Contravariant, PlusEmpty}

/**
  * Created by alonsodomin on 02/01/2016.
  */
trait Predicate[A] extends (A => Boolean) { self =>

  def apply(a: A): Boolean

  def and(m: => Predicate[A]): Predicate[A] = Predicate { a => self(a) && m(a) }
  def or(m: => Predicate[A]): Predicate[A] = Predicate { a => self(a) || m(a) }

  def &&(m: => Predicate[A]): Predicate[A] = and(m)
  def ||(m: => Predicate[A]): Predicate[A] = or(m)

  def unary_! : Predicate[A] = not(self)

}

object Predicate {

  def apply[A](f: A => Boolean): Predicate[A] = new Predicate[A] {
    def apply(a: A): Boolean = f(a)
  }

  implicit val contravariant = new Contravariant[Predicate] {

    def contramap[A, B](fa: Predicate[A])(f: B => A): Predicate[B] =
      Predicate { b => fa(f(b)) }

  }

  object conjunction extends PlusEmpty[Predicate] {
    def empty[A]: Predicate[A] = always(true)

    def plus[A](x: Predicate[A], y: => Predicate[A]): Predicate[A] = x && y
  }

  object disjunction extends PlusEmpty[Predicate] {
    def empty[A]: Predicate[A] = always(false)

    def plus[A](x: Predicate[A], y: => Predicate[A]): Predicate[A] = x || y
  }

}
