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

private[syntax] trait CronStringSyntax {
  import CronStringInterpolator._

  implicit def toCronStringInterpolator(sc: StringContext): CronStringContext =
    new CronStringContext(sc)

  implicit val embedCronStrings = CronStringInterpolator.embed[String](
    Case(DummyCtx, DummyCtx)(identity)
  )

}

object CronStringInterpolator extends Interpolator {
  case object DummyCtx extends Context

  type Input       = String
  type Output      = CronExpr
  type ContextType = DummyCtx.type

  def check(input: String) = Cron.parse(input).leftMap {
    case parseErr: ParseFailed => parseErr.position -> parseErr.getMessage
    case other: Error          => 0                 -> other.getMessage
  }

  def contextualize(interpolation: StaticInterpolation): Seq[ContextType] = {
    val literals = interpolation.parts.collect {
      case lit: Literal => lit
      case hole: Hole =>
        interpolation.abort(hole, "cron: substitutions are not supported")
    }

    if (literals.isEmpty) {
      interpolation.abort(Literal(0, ""), 0, "cron: empty expressions are not allowed")
    } else {
      val lit @ Literal(_, str) = literals.head
      check(str) match {
        case Left((pos, error)) =>
          interpolation.abort(lit, pos, error)
        case Right(_) => Nil
      }
    }
  }

  def evaluate(contextual: RuntimeInterpolation): CronExpr =
    check(contextual.parts.mkString) match {
      case Right(value)   => value
      case Left((pos, _)) => throw new Exception(s"Error parsing cron expression at position: $pos")
    }

}

private[syntax] final class CronStringContext(val sc: StringContext) extends AnyVal {
  def cron = Prefix(CronStringInterpolator, sc)
}

object string extends CronStringSyntax
