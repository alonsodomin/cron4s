package cron4s.expr

import cron4s.core.{Indexed, Bound, Sequential}

/**
  * Created by alonsodomin on 07/11/2015.
  */
sealed trait CronUnit
object CronUnit {

  case object Minute extends CronUnit
  case object Hour extends CronUnit
  case object DayOfMonth extends CronUnit
  case object Month extends CronUnit
  case object DayOfWeek extends CronUnit

}

sealed abstract class CronUnitOps[T: Value, U <: CronUnit] extends Sequential[T] with Bound[T] with Indexed[T] with PartialOrdering[T] {
  def apply(index: Int): Option[T] = {
    if (index < 0 || index >= size) None
    else Some(values(index))
  }

  def unit: U
  def size: Int

  def lteq(lhs: T, rhs: T): Boolean =
    tryCompare(lhs, rhs).exists(_ <= 0)

  val values: IndexedSeq[T]
}

object CronUnitOps {
  import CronUnit._

  private[expr] abstract class NumericCronUnitOps[U <: CronUnit](val min: Int, val max: Int, val unit: U) extends CronUnitOps[Int, U] {

    def tryCompare(lhs: Int, rhs: Int): Option[Int] = {
      if ((lhs < min || lhs > max) || (rhs < min || rhs > max)) None
      else Some(lhs compare rhs)
    }

    def step(v: Int, amount: Int): Option[(Int, Int)] = {
      if (v < min || v > max) None
      else {
        val cursor = (v - min) + amount
        val newIdx = cursor % size
        val newValue = if (newIdx < 0) (max + min) + newIdx else min + newIdx
        Some(newValue, cursor / size)
      }
    }

    def indexOf(v: Int): Option[Int] = {
      if (v < min || v > max) None
      else Some(v - min)
    }

    def size: Int = (max - min) + 1

    val values: IndexedSeq[Int] = min to max

  }

  private[expr] abstract class TextCronUnitOps[U <: CronUnit](val unit: U) extends CronUnitOps[String, U] {

    lazy val min = values(0)
    lazy val max = values(values.size - 1)

    def tryCompare(lhs: String, rhs: String): Option[Int] = {
      (indexOf(lhs), indexOf(rhs)) match {
        case (Some(lhsIdx), Some(rhsIdx)) => Some(lhsIdx compare rhsIdx)
        case _ => None
      }
    }

    def step(v: String, amount: Int): Option[(String, Int)] = {
      if (!values.contains(v)) None
      else {
        val idx = values.indexOf(v)
        val cursor = idx + amount
        val newIdx = cursor % values.size
        val newValue = if (newIdx < 0) values(size + newIdx) else values(newIdx)
        Some(newValue, cursor / values.size)
      }
    }

    def indexOf(v: String): Option[Int] = {
      if (!values.contains(v)) None
      else Some(values.indexOf(v))
    }

    def size: Int = values.size

  }

  implicit object MinuteOps extends NumericCronUnitOps[Minute.type](0, 59, Minute)
  implicit object HourOps extends NumericCronUnitOps[Hour.type](0, 23, Hour)
  implicit object DayOfMonthOps extends NumericCronUnitOps[DayOfMonth.type](1, 31, DayOfMonth)

  implicit object NumericMonthOps extends NumericCronUnitOps[Month.type](1, 12, Month)
  implicit object TextMonthOps extends TextCronUnitOps[Month.type](Month) {
    val values = IndexedSeq("jan", "feb", "mar", "apr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dec")
  }

  implicit object NumericDayOfWeekOps extends NumericCronUnitOps[DayOfWeek.type](0, 6, DayOfWeek)
  implicit object TextDayOfWeekOps extends TextCronUnitOps[DayOfWeek.type](DayOfWeek) {
    val values = IndexedSeq("mon", "tue", "wed", "thu", "fri", "sat", "sun")
  }
}
