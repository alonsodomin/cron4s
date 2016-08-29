package cron4s

import cron4s.types.HasCronField

import scala.annotation.implicitNotFound
import scala.language.higherKinds

/**
  * Created by alonsodomin on 02/01/2016.
  */
@implicitNotFound("Field ${F} is not supported on Cron expressions")
sealed abstract class CronUnit[F <: CronField] extends Serializable {
  final type FieldType = F

  def field: F

  def range: IndexedSeq[Int]

  //override def toString = s"CronUnit[$field]"

}

object CronUnit extends CronUnitInstances {

  @inline def apply[F <: CronField](implicit unit: CronUnit[F]): CronUnit[F] = unit

  final val All: Seq[CronUnit[_ <: CronField]] = Seq(Seconds, Minutes, Hours, DaysOfMonth, Months, DaysOfWeek)

}

private[cron4s] trait CronUnits {
  import CronField._

  private[cron4s] abstract class AbstractCronUnit[F <: CronField](
    val field: F, val min: Int, val max: Int
  ) extends CronUnit[F] {

    /*def step(from: Int, step: Int): Option[(Int, Int)] = {
      if (from < min || from > max) None
      else Sequential.sequential(range).step(from, step)
    }

    def indexOf(v: Int): Option[Int] = {
      if (v < min || v > max) None
      else Some(v - min)
    }*/

    override def equals(that: Any): Boolean = that match {
      case other: AbstractCronUnit[F] =>
        this.field == other.field && this.range == other.range
      case _ => false
    }

    val range: IndexedSeq[Int] = min to max

  }

  implicit case object Seconds extends AbstractCronUnit[Second.type](Second, 0, 59)
  implicit case object Minutes extends AbstractCronUnit[Minute.type](Minute, 0, 59)
  implicit case object Hours extends AbstractCronUnit[Hour.type](Hour, 0, 23)
  implicit case object DaysOfMonth extends AbstractCronUnit[DayOfMonth.type](DayOfMonth, 1, 31)
  implicit case object Months extends AbstractCronUnit[Month.type](Month, 1, 12) {
    val textValues = IndexedSeq(
      "jan", "feb", "mar",
      "apr", "may", "jun",
      "jul", "ago", "sep",
      "oct", "nov", "dec"
    )
  }
  implicit case object DaysOfWeek extends AbstractCronUnit(DayOfWeek, 0, 6) {
    val textValues = IndexedSeq("mon", "tue", "wed", "thu", "fri", "sat", "sun")
  }

}

private[cron4s] trait CronUnitInstances extends CronUnits {

  private[this] def hasCronField[F <: CronField](unit: CronUnit[F]): HasCronField[CronUnit, F] =
    new HasCronField[CronUnit, F] {
      override def range(fL: CronUnit[F]): IndexedSeq[Int] = unit.range
    }

  implicit val secondsInstance     = hasCronField(Seconds)
  implicit val minutesInstance     = hasCronField(Minutes)
  implicit val hoursInstance       = hasCronField(Hours)
  implicit val daysOfMonthInstance = hasCronField(DaysOfMonth)
  implicit val monthsInstance      = hasCronField(Months)
  implicit val daysOfWeekInstance  = hasCronField(DaysOfWeek)

}
