package cron4s

import cron4s.core.{Bound, Indexed, Sequential}

import scala.annotation.implicitNotFound

/**
  * Created by alonsodomin on 02/01/2016.
  */
@implicitNotFound("Field ${F} is not supported on Cron expressions")
sealed trait CronUnit[F <: CronField]
    extends Sequential[Int] with Bound[Int] with Indexed[Int] {

  def apply(index: Int): Option[Int] = {
    if (index < 0 || index >= size) None
    else Some(range(index))
  }

  def field: F
  def size: Int

  def narrow(min: Int, max: Int): CronUnit[F]

  val range: IndexedSeq[Int]
}

object CronUnit {
  import CronField._

  @inline def apply[F <: CronField](implicit unit: CronUnit[F]): CronUnit[F] = unit

  private[cron4s] abstract class BaseCronUnit[F <: CronField](val min: Int, val max: Int, val field: F) extends CronUnit[F] {

    def step(from: Int, step: Int): Option[(Int, Int)] = {
      if (from < min || from > max) None
      else Sequential.sequential(range).step(from, step)
    }

    def indexOf(v: Int): Option[Int] = {
      if (v < min || v > max) None
      else Some(v - min)
    }

    def size: Int = (max - min) + 1

    def narrow(min: Int, max: Int): CronUnit[F] = new BaseCronUnit[F](min, max, field) {}

    val range: IndexedSeq[Int] = min to max

  }

  implicit object Minutes extends BaseCronUnit[Minute.type](0, 59, Minute)
  implicit object Hours extends BaseCronUnit[Hour.type](0, 23, Hour)
  implicit object DaysOfMonth extends BaseCronUnit[DayOfMonth.type](1, 31, DayOfMonth)
  implicit object Months extends BaseCronUnit[Month.type](1, 12, Month) {
    val textValues = IndexedSeq(
      "jan", "feb", "mar",
      "apr", "may", "jun",
      "jul", "ago", "sep",
      "oct", "nov", "dec"
    )
  }
  implicit object DaysOfWeek extends BaseCronUnit[DayOfWeek.type](0, 6, DayOfWeek) {
    val textValues = IndexedSeq("mon", "tue", "wed", "thu", "fri", "sat", "sun")
  }

}
