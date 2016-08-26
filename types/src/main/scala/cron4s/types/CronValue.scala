package cron4s.types

/**
  * Created by alonsodomin on 23/08/2016.
  */
trait CronValue[T]

object CronValue {
  @inline def apply[T](implicit ev: CronValue[T]): CronValue[T] = ev

  implicit object NumericCronValue extends CronValue[Int]
  implicit object TextualCronValue extends CronValue[String]
}
