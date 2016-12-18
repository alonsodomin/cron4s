package cron4s

/**
  * Each of the different fields supported in CRON expressions
  *
  * @author Antonio Alonso Dominguez
  */
sealed trait CronField extends Serializable
object CronField {

  sealed trait Second extends CronField
  case object Second extends Second

  sealed trait Minute extends CronField
  case object Minute extends Minute

  sealed trait Hour extends CronField
  case object Hour extends Hour

  sealed trait DayOfMonth extends CronField
  case object DayOfMonth extends DayOfMonth

  sealed trait Month extends CronField
  case object Month extends Month

  sealed trait DayOfWeek extends CronField
  case object DayOfWeek extends DayOfWeek

  final val All: Seq[CronField] = Seq(Second, Minute, Hour, DayOfMonth, Month, DayOfWeek)

}
