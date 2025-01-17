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

private[cron4s] object ParserAdapter {

  import cron4s.parser._
  import cron4s.parser.Node._

  def adapt(parser: Parser)(input: String): Either[cron4s.Error, cron4s.CronExpr] =
    parser
      .parse(input)
      .fold(e => Left(mapError(e)), v => Right(mapExpr(v)))

  private def mapError(error: cron4s.parser.Error): cron4s.Error =
    error match {
      case ExprTooShort => cron4s.ExprTooShort
      case e: ParseFailed =>
        cron4s.ParseFailed(expected = e.expected, position = e.position, found = e.found)
    }

  private def mapExpr(expr: CronExpr): cron4s.CronExpr = {
    cron4s.expr.CronExpr(
      seconds = mapNode[CronField.Second](expr.seconds),
      minutes = mapNode[CronField.Minute](expr.minutes),
      hours = mapNode[CronField.Hour](expr.hours),
      daysOfMonth = mapNodeWithAny[CronField.DayOfMonth](expr.daysOfMonth),
      months = mapNode[CronField.Month](expr.months),
      daysOfWeek = mapNodeWithAny[CronField.DayOfWeek](expr.daysOfWeek)
    )
  }

  private def mapNode[F <: cron4s.CronField](
      node: NodeWithoutAny
  )(implicit unit: cron4s.CronUnit[F]): cron4s.expr.FieldNode[F] =
    node match {
      case Node.EachNode       => cron4s.expr.EachNode[F]
      case n: Node.ConstNode   => mapConst[F](n)
      case n: Node.BetweenNode => mapBetweenNode[F](n)
      case n: Node.SeveralNode => mapSeveral[F](n)
      case n: Node.EveryNode   => cron4s.expr.EveryNode[F](mapDivisible(n.base), n.freq)
    }

  private def mapNodeWithAny[F <: cron4s.CronField](
      node: Node
  )(implicit unit: cron4s.CronUnit[F]): cron4s.expr.FieldNodeWithAny[F] =
    node match {
      case Node.EachNode       => cron4s.expr.EachNode[F]
      case Node.AnyNode        => cron4s.expr.AnyNode[F]
      case n: Node.ConstNode   => mapConst[F](n)
      case n: Node.BetweenNode => mapBetweenNode[F](n)
      case n: Node.SeveralNode => mapSeveral[F](n)
      case n: Node.EveryNode   => cron4s.expr.EveryNode[F](mapDivisible(n.base), n.freq)
    }

  private def mapDivisible[F <: CronField](divisible: DivisibleNode)(implicit
      unit: cron4s.CronUnit[F]
  ): cron4s.expr.DivisibleNode[F] =
    divisible match {
      case n: Node.BetweenNode => mapBetweenNode[F](n)
      case Node.EachNode       => cron4s.expr.EachNode[F]
      case n: Node.SeveralNode => mapSeveral[F](n)
    }

  private def mapSeveral[F <: CronField](n: Node.SeveralNode)(implicit
      unit: cron4s.CronUnit[F]
  ): cron4s.expr.SeveralNode[F] = cron4s.expr.SeveralNode.apply[F](
    first = mapEnumerable[F](n.head),
    second = mapEnumerable[F](n.tail.head),
    tail = n.tail.tail.map(node => mapEnumerable[F](node)): _*
  )

  private def mapEnumerable[F <: CronField](enumerable: EnumerableNode)(implicit
      unit: cron4s.CronUnit[F]
  ): cron4s.expr.EnumerableNode[F] =
    enumerable match {
      case n: Node.BetweenNode => mapBetweenNode[F](n)
      case n: Node.ConstNode   => mapConst[F](n)
    }

  private def mapBetweenNode[F <: CronField](n: Node.BetweenNode)(implicit
      unit: cron4s.CronUnit[F]
  ): cron4s.expr.BetweenNode[F] = {
    cron4s.expr.BetweenNode(
      begin = mapConst[F](n.begin),
      end = mapConst[F](n.end)
    )
  }

  private def mapConst[F <: CronField](n: Node.ConstNode)(implicit
      unit: cron4s.CronUnit[F]
  ): cron4s.expr.ConstNode[F] =
    cron4s.expr.ConstNode(value = n.value, textValue = n.textValue)

}
