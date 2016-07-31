package cron4s.ext

import cron4s.CronField

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait DateTimeAdapter[DateTime] {

  def extract[F <: CronField](dateTime: DateTime, field: F): Option[Int]

  def adjust[F <: CronField](dateTime: DateTime, field: F, value: Int): Option[DateTime]

}
