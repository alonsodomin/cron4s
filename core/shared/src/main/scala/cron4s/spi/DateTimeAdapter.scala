package cron4s.spi

import cron4s.CronField

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait DateTimeAdapter[DateTime] {

  def get[F <: CronField](dateTime: DateTime, field: F): Option[Int]

  def set[F <: CronField](dateTime: DateTime, field: F, value: Int): Option[DateTime]

}
