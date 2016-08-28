package cron4s

/**
  * Created by alonsodomin on 07/11/2015.
  */
sealed trait CronField extends Serializable
object CronField {

  case object Minute extends CronField
  case object Hour extends CronField
  case object DayOfMonth extends CronField
  case object Month extends CronField
  case object DayOfWeek extends CronField

  final val All: Seq[CronField] = Seq(Minute, Hour, DayOfMonth, Month, DayOfWeek)

}
