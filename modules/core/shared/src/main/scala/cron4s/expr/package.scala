package cron4s

import cats.{Eq, Show}
import cats.derived

import cron4s.internal.base._

import shapeless._

package object expr {
  import CronField._

  type RangeNode[F <: CronField] =
    EachInRange[F] :+: ConstValue[F] :+: BoundedRange[F] :+: EnumeratedRange[F] :+: SteppingRange[F] :+: CNil

  implicit def rangeNodeEq[F <: CronField]: Eq[RangeNode[F]] =
    derived.semi.eq[RangeNode[F]]

  implicit def rangeNodeShow[F <: CronField]: Show[RangeNode[F]] =
    Show.show(_.fold(ops.show))

  type WildcardRangeNode[F <: CronField] = AnyInRange[F] :+: RangeNode[F]

  implicit def wildcardRangeNodeEq[F <: CronField]: Eq[WildcardRangeNode[F]] =
    derived.semi.eq[WildcardRangeNode[F]]

  implicit def wildcardRangeNodeShow[F <: CronField]: Show[WildcardRangeNode[F]] =
    Show.show(_.fold(ops.show))

  type SecondsNode     = RangeNode[Second]
  type MinutesNode     = RangeNode[Minute]
  type HoursNode       = RangeNode[Hour]
  type DaysOfMonthNode = WildcardRangeNode[DayOfMonth]
  type MonthsNode      = RangeNode[Month]
  type DaysOfWeekNode  = WildcardRangeNode[DayOfWeek]

  // implicit def rangeNodeSteppable[F <: CronField, DT](
  //     implicit
  //     E0: Enumerated[CronRange[F]],
  //     DT: IsDateTime[DT]
  // ): Steppable[RangeNode[F], DT] = new Steppable[RangeNode[F], DT] {
  //   def narrowNode(node: RangeNode[F], from: DT): Either[DateTimeError, Enumerated[CronRange[F]]] =
  //     for {
  //       next <- DT.next(from, ops.unit(node).field)
  //       prev <- DT.prev(from, ops.unit(node).field)
  //       min  <- DT.first(next, ops.unit(node).field)
  //       max  <- DT.last(prev, ops.unit(node).field)
  //     } yield E0.withMin(min).withMax(max)

  //   def step(node: RangeNode[F], from: DT, step: Step): Either[ExprError, (DT, Int)] =
  //     for {
  //       enum                  <- narrowNode(node, from)
  //       currValue             <- DT.get(from, ops.unit(node).field)
  //       (newValue, carryOver) <- enum.step(ops.range(node), currValue, step)
  //       newResult             <- DT.set(from, ops.unit(node).field, newValue)
  //     } yield (newResult, carryOver)
  // }
}
