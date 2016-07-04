package cron4s.matcher

import cats.{Eq, Monoid}
import cats.Foldable
import cats.functor.Contravariant

/**
  * Created by alonsodomin on 02/01/2016.
  */
trait Matcher[A] { self =>

  def matches(a: A): Boolean

  def and(m: => Matcher[A]): Matcher[A] = Matcher { a => self.matches(a) && m.matches(a) }
  def or(m: => Matcher[A]): Matcher[A] = Matcher { a => self.matches(a) || m.matches(a) }

  def &&(m: => Matcher[A]): Matcher[A] = and(m)
  def ||(m: => Matcher[A]): Matcher[A] = or(m)

  def unary_! : Matcher[A] = Matcher.not(self)

}

object Matcher {

  def apply[A](f: A => Boolean): Matcher[A] = new Matcher[A] {
    def matches(a: A): Boolean = f(a)
  }

  def not[A](m: Matcher[A]): Matcher[A] = Matcher { a => !m.matches(a) }

  def equal[A: Eq](a: A): Matcher[A] = Matcher { b => implicitly[Eq[A]].eqv(a, b) }

  def none[C[_], A](c: C[Matcher[A]])(implicit ev: Foldable[C]): Matcher[A] =
    not(forall(c))

  def exists[C[_], A](c: C[Matcher[A]])(implicit ev: Foldable[C]): Matcher[A] =
    Matcher { a => ev.exists(c)(_.matches(a)) }

  def forall[C[_], A](c: C[Matcher[A]])(implicit ev: Foldable[C]): Matcher[A] =
    Matcher { a => ev.forall(c)(_.matches(a)) }

  implicit val contravariant = new Contravariant[Matcher] {

    def contramap[A, B](fa: Matcher[A])(f: B => A): Matcher[B] =
      Matcher { b => fa.matches(f(b)) }

  }

  object disjunction {

    implicit def monoid[A] = new Monoid[Matcher[A]] {
      def empty: Matcher[A] = Matcher { _ => true }

      def combine(x: Matcher[A], y: Matcher[A]): Matcher[A] = x && y

    }

  }

  object conjunction {

    implicit def monoid[A] = new Monoid[Matcher[A]] {
      def empty: Matcher[A] = Matcher { _ => false }

      def combine(x: Matcher[A], y: Matcher[A]): Matcher[A] = x || y
    }

  }

}
