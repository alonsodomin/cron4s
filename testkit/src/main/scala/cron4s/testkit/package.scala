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

import scalaz.Equal

import org.scalacheck._
import org.scalacheck.util.Pretty
import Prop.{False, Proof, Result}

/**
  * Blantanly copied from
  * https://github.com/typelevel/cats/blob/master/kernel-laws/src/main/scala/cats/kernel/laws/package.scala
  */
package object testkit {
  lazy val proved = Prop(Result(status = Proof))

  lazy val falsified = Prop(Result(status = False))

  object Ops {
    def run[A](sym: String)(lhs: A, rhs: A)(f: (A, A) => Boolean): Prop =
      if (f(lhs, rhs)) proved else falsified :| {
        val exp = Pretty.pretty(lhs, Pretty.Params(0))
        val got = Pretty.pretty(rhs, Pretty.Params(0))
        s"($exp $sym $got) failed"
      }
  }

  implicit class CheckEqOps[A](lhs: A)(implicit ev: Equal[A], pp: A => Pretty) {
    def ?==(rhs: A): Prop = Ops.run("?==")(lhs, rhs)(ev.equal)
    def ?!=(rhs: A): Prop = Ops.run("?!=")(lhs, rhs)((l, r) => !ev.equal(l, r))
  }

  /*implicit class CheckOrderOps[A](lhs: A)(implicit ev: PartialOrder[A], pp: A => Pretty) {
    def ?<(rhs: A): Prop = Ops.run("?<")(lhs, rhs)(ev.lt)
    def ?<=(rhs: A): Prop = Ops.run("?<=")(lhs, rhs)(ev.lteqv)
    def ?>(rhs: A): Prop = Ops.run("?>")(lhs, rhs)(ev.gt)
    def ?>=(rhs: A): Prop = Ops.run("?>=")(lhs, rhs)(ev.gteqv)
  }*/

  implicit final class BooleanOps[A](lhs: Boolean)(implicit pp: Boolean => Pretty) {
    def ?&&(rhs: Boolean): Prop = Ops.run("?&&")(lhs, rhs)(_ && _)
    def ?||(rhs: Boolean): Prop = Ops.run("?||")(lhs, rhs)(_ || _)
  }

  implicit final class IsEqualArrow[A](val lhs: A) extends AnyVal {
    def <->(rhs: A): IsEqual[A] = IsEqual(lhs, rhs)
  }

}
