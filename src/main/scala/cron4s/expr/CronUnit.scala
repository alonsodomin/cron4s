package cron4s.expr

import cron4s.core.Sequential

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

sealed abstract class CronUnitOps[T: Value, U <: CronUnit] extends Sequential[T] {
  def unit: U
}

object CronUnitOps {
  import CronUnit._

  private abstract class NumericCronUnitOps[U <: CronUnit](val min: Int, val max: Int, val unit: U) extends CronUnitOps[Int, U] {

    private[this] def totalUnits: Int = (max - min) + 1

    def forward(v: Int, amount: Int): Option[(Int, Int)] = {
      if (v < min || v > max) None
      else {
        val advanced = Math.abs(v + amount)
        Some(advanced % totalUnits, advanced / totalUnits)
      }
    }

  }

  private abstract class TextCronUnitOps[U <: CronUnit](val unit: CronUnit) extends CronUnitOps[String, U] {

    lazy val min = values(0)
    lazy val max = values(values.size - 1)

    def forward(v: String, amount: Int): Option[(String, Int)] = {
      if (!values.contains(v)) None
      else {
        val idx = values.indexOf(v)
        val cursor = Math.abs(idx + amount)
        Some(values(cursor % values.size), cursor / values.size)
      }
    }

    val values: IndexedSeq[String]

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
