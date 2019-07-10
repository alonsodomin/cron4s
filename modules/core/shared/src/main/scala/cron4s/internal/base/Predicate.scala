/*
 * Copyright 2017 Antonio Alonso Dominguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cron4s
package internal
package base

import cats.{Eq, Monoid, MonoidK, Foldable, Contravariant}
import cats.syntax.foldable._

/**
  * Created by alonsodomin on 02/01/2016.
  */
private[cron4s] trait Predicate[A] extends (A => Boolean) { self =>

  def apply(a: A): Boolean

  def and(m: => Predicate[A]): Predicate[A] = Predicate { a =>
    self(a) && m(a)
  }
  def or(m: => Predicate[A]): Predicate[A] = Predicate { a =>
    self(a) || m(a)
  }

  def &&(m: => Predicate[A]): Predicate[A] = and(m)
  def ||(m: => Predicate[A]): Predicate[A] = or(m)

  def unary_! : Predicate[A] = Predicate.not(self)

}

private[cron4s] object Predicate extends Predicates {

  def apply[A](f: A => Boolean): Predicate[A] = new Predicate[A] {
    def apply(a: A): Boolean = f(a)
  }

  implicit val contravariant = new Contravariant[Predicate] {

    def contramap[A, B](fa: Predicate[A])(f: B => A): Predicate[B] =
      Predicate { b =>
        fa(f(b))
      }

  }

  object conjunction {
    implicit val monoidK: MonoidK[Predicate] = new MonoidK[Predicate] {
      def empty[A]: Predicate[A] = always(true)

      def combineK[A](x: Predicate[A], y: Predicate[A]): Predicate[A] = x && y
    }

    implicit def monoid[A]: Monoid[Predicate[A]] = monoidK.algebra[A]
  }

  object disjunction {
    implicit val monoidK: MonoidK[Predicate] = new MonoidK[Predicate] {
      def empty[A]: Predicate[A] = always(false)

      def combineK[A](x: Predicate[A], y: Predicate[A]): Predicate[A] = x || y
    }

    implicit def monoid[A]: Monoid[Predicate[A]] = monoidK.algebra[A]
  }

}

private[cron4s] trait Predicates {

  def always[A](value: => Boolean): Predicate[A] = Predicate { _ =>
    value
  }

  def not[A](m: Predicate[A]): Predicate[A] = Predicate { a =>
    !m(a)
  }

  def equalTo[A: Eq](a: A): Predicate[A] = Predicate { b =>
    Eq[A].eqv(a, b)
  }

  def noneOf[C[_], A](c: C[Predicate[A]])(implicit ev: Foldable[C]): Predicate[A] =
    not(allOf(c))

  def anyOf[C[_], A](c: C[Predicate[A]])(implicit ev: Foldable[C]): Predicate[A] =
    Predicate { a =>
      ev.exists(c)(_(a))
    }

  def allOf[C[_], A](c: C[Predicate[A]])(implicit ev: Foldable[C]): Predicate[A] =
    Predicate { a =>
      ev.forall(c)(_(a))
    }

  def asOf[C[_]: Foldable, A](c: C[Predicate[A]])(implicit M: MonoidK[Predicate]): Predicate[A] =
    c.foldLeft(M.empty[A])((a, b) => M.combineK(a, b))

}