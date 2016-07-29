package cron4s.matcher

import cron4s.types.{MonoidK, Contravariant}

/**
  * Created by alonsodomin on 02/01/2016.
  */
trait Matcher[A] extends (A => Boolean) { self =>

  def apply(a: A): Boolean

  def and(m: => Matcher[A]): Matcher[A] = Matcher { a => self(a) && m(a) }
  def or(m: => Matcher[A]): Matcher[A] = Matcher { a => self(a) || m(a) }

  def &&(m: => Matcher[A]): Matcher[A] = and(m)
  def ||(m: => Matcher[A]): Matcher[A] = or(m)

  def unary_! : Matcher[A] = not(self)

}

object Matcher {

  def apply[A](f: A => Boolean): Matcher[A] = new Matcher[A] {
    def apply(a: A): Boolean = f(a)
  }

  implicit val contravariant = new Contravariant[Matcher] {

    def contramap[A, B](fa: Matcher[A])(f: B => A): Matcher[B] =
      Matcher { b => fa(f(b)) }

  }

  object disjunction {

    implicit val monoid = new MonoidK[Matcher] {
      def empty[A]: Matcher[A] = Matcher { _ => true }

      def combineK[A](x: Matcher[A], y: Matcher[A]): Matcher[A] = x && y
    }

  }

  object conjunction {

    implicit val monoid = new MonoidK[Matcher] {
      def empty[A]: Matcher[A] = Matcher { _ => false }

      def combineK[A](x: Matcher[A], y: Matcher[A]): Matcher[A] = x || y
    }

  }

}
