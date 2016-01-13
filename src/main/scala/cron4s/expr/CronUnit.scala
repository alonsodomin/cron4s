package cron4s.expr

import cron4s.core.{Indexed, Bound, Sequential}
import cron4s.matcher.Matcher
import simulacrum.typeclass

import scala.annotation.implicitNotFound

import algebra.std.int._

/**
  * Created by alonsodomin on 02/01/2016.
  */
@implicitNotFound("Field ${F} is not supported on Cron expressions")
sealed trait CronUnit[F <: CronField]
    extends Sequential[Int] with Bound[Int] with Indexed[Int] with PartialOrdering[Int] { self =>

  def apply(index: Int): Option[Int] = {
    if (index < 0 || index >= size) None
    else Some(values(index))
  }

  def `match`(x: Int)(implicit ev: CronUnit[F]): Matcher[Int] = Matcher.equal(x)

  def field: F
  def size: Int

  def lteq(lhs: Int, rhs: Int): Boolean =
    tryCompare(lhs, rhs).exists(_ <= 0)

  def focus(min: Int, max: Int): CronUnit[F]

  val values: IndexedSeq[Int]
}

object CronUnit {
  import CronField._

  private[expr] abstract class BaseCronUnit[F <: CronField](val min: Int, val max: Int, val field: F) extends CronUnit[F] {

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

    def focus(min: Int, max: Int): CronUnit[F] = new BaseCronUnit[F](min, max, field) {}

    val values: IndexedSeq[Int] = min to max

  }

  implicit object MinutesUnit extends BaseCronUnit[Minute.type](0, 59, Minute)
  implicit object HoursUnit extends BaseCronUnit[Hour.type](0, 23, Hour)
  implicit object DaysOfMonthUnit extends BaseCronUnit[DayOfMonth.type](1, 31, DayOfMonth)

  implicit object MonthsUnit extends BaseCronUnit[Month.type](1, 12, Month) {

    val namedValues = IndexedSeq("jan", "feb", "mar", "apr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dec")

  }

  implicit object DaysOfWeekUnit extends BaseCronUnit[DayOfWeek.type](0, 6, DayOfWeek) {
    val namedValues = IndexedSeq("mon", "tue", "wed", "thu", "fri", "sat", "sun")
  }

}

