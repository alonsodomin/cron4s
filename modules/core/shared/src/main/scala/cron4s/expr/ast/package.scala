package cron4s
package expr

import cron4s.base._
import cron4s.datetime.IsDateTime

import shapeless._

package object ast {
  import CronField._

  private[ast] type RangeNode[F <: CronField] =
    EachInRange[F] :+: ConstValue[F] :+: BoundedRange[F] :+: EnumeratedRange[F] :+: SteppingRange[F] :+: CNil
  private[ast] type WildcardRangeNode[F <: CronField] =
    AnyInRange[F] :+: RangeNode[F]

  type SecondsNode     = RangeNode[Second]
  type MinutesNode     = RangeNode[Minute]
  type HoursNode       = RangeNode[Hour]
  type DaysOfMonthNode = WildcardRangeNode[DayOfMonth]
  type MonthsNode      = RangeNode[Month]
  type DaysOfWeekNode  = WildcardRangeNode[DayOfWeek]

  implicit def rangeNodeSteppable[F <: CronField, DT](
      implicit
      E0: Enumerated[CronRange[F]],
      DT: IsDateTime[DT]
  ): Steppable[RangeNode[F], DT] = new Steppable[RangeNode[F], DT] {
    def narrowNode(node: RangeNode[F], from: DT): Either[DateTimeError, Enumerated[CronRange[F]]] =
      for {
        next <- DT.next(from, ops.unit(node).field)
        prev <- DT.prev(from, ops.unit(node).field)
        min  <- DT.first(next, ops.unit(node).field)
        max  <- DT.last(prev, ops.unit(node).field)
      } yield E0.withMin(min).withMax(max)

    def step(node: RangeNode[F], from: DT, step: Step): Either[ExprError, (DT, Int)] =
      for {
        enum                  <- narrowNode(node, from)
        currValue             <- DT.get(from, ops.unit(node).field)
        (newValue, carryOver) <- enum.step(ops.range(node), currValue, step)
        newResult             <- DT.set(from, ops.unit(node).field, newValue)
      } yield (newResult, carryOver)
  }
}