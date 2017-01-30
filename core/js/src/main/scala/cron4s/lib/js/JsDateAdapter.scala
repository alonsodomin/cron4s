package cron4s.lib.js

import cron4s.CronField
import cron4s.CronField._
import cron4s.datetime.DateTimeAdapter

import scala.scalajs.js.Date

/**
  * Created by alonsodomin on 30/01/2017.
  */
private[js] final class JsDateAdapter extends DateTimeAdapter[Date] {

  override def get[F <: CronField](dateTime: Date, field: F): Option[Int] = {
    val value = field match {
      case Second     => dateTime.getSeconds()
      case Minute     => dateTime.getMinutes()
      case Hour       => dateTime.getHours()
      case DayOfMonth => dateTime.getDate()
      case Month      => dateTime.getMonth() + 1
      case DayOfWeek  =>
        val dayOfWeek = {
          val idx = dateTime.getDay() - 1
          if (idx < 0) 7 + idx
          else idx
        }
        dayOfWeek
    }

    Some(value)
  }

  override def set[F <: CronField](dateTime: Date, field: F, value: Int): Option[Date] = {
    def setter(setter: Date => Unit): Date = {
      val newDateTime = new Date(dateTime.getTime())
      setter(newDateTime)
      newDateTime
    }

    Some(field match {
      case Second     => setter(_.setSeconds(value))
      case Minute     => setter(_.setMinutes(value))
      case Hour       => setter(_.setHours(value))
      case DayOfMonth => setter(_.setDate(value))
      case Month      => setter(_.setMonth(value - 1))
      case DayOfWeek  =>
        val dayToSet = (value + 1) % 7
        val offset = dayToSet - dateTime.getDay()
        setter(d => d.setDate(d.getDate() + offset))
    })
  }
}

}
