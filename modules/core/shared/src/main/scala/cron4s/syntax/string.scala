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
package syntax

import cats.syntax.either._

import contextual._

private[cron4s] sealed trait CronInterpolatorCtx extends Context
private[cron4s] object CronInterpolatorCtx {
  case object Plain    extends CronInterpolatorCtx
  case object ToString extends CronInterpolatorCtx
}

private[syntax] trait CronStringSyntax {
  import CronInterpolatorCtx._

  implicit def toCronStringInterpolator(sc: StringContext): CronStringContext =
    new CronStringContext(sc)

  implicit val embedCronStrings = CronStringInterpolator.embed[String](
    Case(Plain, Plain)(identity)
  )

}

object CronStringInterpolator extends Interpolator {
  type Output      = CronExpr
  type Input       = String
  type ContextType = CronInterpolatorCtx

  def check(input: String) = Cron.parse(input).leftMap {
    case parseErr: ParseFailed => parseErr.position -> parseErr.getMessage
    case other: Error          => 0                 -> other.getMessage
  }

  def contextualize(interpolation: StaticInterpolation): Seq[ContextType] =
    interpolation.parts.flatMap {
      case hole @ Hole(_, _) => Seq(CronInterpolatorCtx.Plain)
      case _                 => Nil
    }

  // def contextualize(interpolation: StaticInterpolation): Seq[ContextType] = {
  //   interpolation.parts.foreach {
  //     case lit @ Literal(_, string) =>
  //       println(s"lit: $string")
  //       check(string) match {
  //         case Left((pos, error)) =>
  //           interpolation.abort(lit, pos, error)
  //         case Right(_) =>
  //           println("Incomplete parsing completed??")
  //           ()
  //       }

  //     case hole @ Hole(_, _) =>
  //       println(s"hole: $hole")
  //       interpolation.abort(hole, "substitutions are not permitted")
  //   }
  //   Nil
  // }

  def evaluate(contextual: RuntimeInterpolation): CronExpr =
    Cron.unsafeParse(contextual.parts.mkString)

}

private[syntax] final class CronStringContext(val sc: StringContext) extends AnyVal {
  def cron = Prefix(CronStringInterpolator, sc)
}

object string extends CronStringSyntax
