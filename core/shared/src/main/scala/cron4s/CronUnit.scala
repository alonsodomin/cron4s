package cron4s

import cron4s.types.Enumerated

import scala.annotation.implicitNotFound
import scala.language.higherKinds

/**
  * A Cron Unit is the representation of valid values that are accepted
  * at a given Cron Field.
  *
  * @author Antonio Alonso Dominguez
  */
@implicitNotFound("Field ${F} is not supported on Cron expressions")
sealed trait CronUnit[+F <: CronField] extends Serializable {

  /**
    * @return the CronField for this unit
    */
  def field: F

  /**
    * Cron units have a range of valid values
    *
    * @return the range of valid values
    */
  def range: IndexedSeq[Int]

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

    val range: IndexedSeq[Int] = min to max

  }

  implicit case object Seconds extends AbstractCronUnit[Second](Second, 0, 59)
  implicit case object Minutes extends AbstractCronUnit[Minute](Minute, 0, 59)
  implicit case object Hours extends AbstractCronUnit[Hour](Hour, 0, 23)
  implicit case object DaysOfMonth extends AbstractCronUnit[DayOfMonth](DayOfMonth, 1, 31)
  implicit case object Months extends AbstractCronUnit[Month](Month, 1, 12) {
    val textValues = IndexedSeq(
      "jan", "feb", "mar",
      "apr", "may", "jun",
      "jul", "ago", "sep",
      "oct", "nov", "dec"
    )
  }
  implicit case object DaysOfWeek extends AbstractCronUnit[DayOfWeek](DayOfWeek, 0, 6) {
    val textValues = IndexedSeq("mon", "tue", "wed", "thu", "fri", "sat", "sun")
  }

}

private[cron4s] trait CronUnitInstances extends CronUnits {

  private[this] def enumerated[F <: CronField](unit: CronUnit[F]): Enumerated[CronUnit[F]] =
    new Enumerated[CronUnit[F]] {
      override def range(fL: CronUnit[F]): IndexedSeq[Int] = unit.range
    }

  implicit val secondsInstance     = enumerated(Seconds)
  implicit val minutesInstance     = enumerated(Minutes)
  implicit val hoursInstance       = enumerated(Hours)
  implicit val daysOfMonthInstance = enumerated(DaysOfMonth)
  implicit val monthsInstance      = enumerated(Months)
  implicit val daysOfWeekInstance  = enumerated(DaysOfWeek)

}
