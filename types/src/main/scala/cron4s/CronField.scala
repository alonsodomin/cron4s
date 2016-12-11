package cron4s

/**
  * Each of the different fields supported in CRON expressions
  *
  * @author Antonio Alonso Dominguez
  */
sealed trait CronField extends Serializable
object CronField {

  case object Second extends CronField
  case object Minute extends CronField
  case object Hour extends CronField
  case object DayOfMonth extends CronField
  case object Month extends CronField
  case object DayOfWeek extends CronField

  final val All: Seq[CronField] = Seq(Second, Minute, Hour, DayOfMonth, Month, DayOfWeek)

}
