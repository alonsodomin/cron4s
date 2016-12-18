package cron4s.spi

import cron4s.CronField

/**
  * Bridge adapter between specific date-time libraries and expression support
  *
  * @author Antonio Alonso Dominguez
  */
trait DateTimeAdapter[DateTime] {

  /**
    * Getter access for a specific field in a date-time
    *
    * @param dateTime a date-time
    * @param field a CronField
    * @tparam F the CronField type
    * @return value of the field
    */
  def get[F <: CronField](dateTime: DateTime, field: F): Option[Int]

  /**
    * Setter access for a specific field in a date-time
    *
    * @param dateTime a date-time
    * @param field a CronField
    * @param value new value for the field
    * @tparam F the CronField type
    * @return a new date-time with the given field set to the new value
    */
  def set[F <: CronField](dateTime: DateTime, field: F, value: Int): Option[DateTime]

}

object DateTimeAdapter {
  def apply[DateTime](implicit ev: DateTimeAdapter[DateTime]): DateTimeAdapter[DateTime] = ev
}
