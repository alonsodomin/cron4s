package cron4s.testkit.laws

import cron4s.CronField
import cron4s.ext.DateTimeAdapter
import cron4s.testkit.CronFieldValue

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait DateTimeAdapterLaws[DateTime <: AnyRef] {
  implicit def adapter: DateTimeAdapter[DateTime]
  implicit val eq: Equal[DateTime]

  def immutability[F <: CronField](dt: DateTime, fieldValue: CronFieldValue[F]): Boolean = {
    val currentVal = adapter.get(dt, fieldValue.field)
    val newDateTime = adapter.set(dt, fieldValue.field, fieldValue.value)

    currentVal.flatMap(current => newDateTime.map { ndt =>
      if (current === fieldValue.value) ndt === dt
      else ndt =/= dt
    }).exists(identity)
  }

  def settable[F <: CronField](dt: DateTime, fieldValue: CronFieldValue[F]): Boolean = {
    val newDateTime = adapter.set(dt, fieldValue.field, fieldValue.value)
    newDateTime.flatMap(ndt => adapter.get(ndt, fieldValue.field)).exists(_ === fieldValue.value)
  }

}

object DateTimeAdapterLaws {

  def apply[DateTime <: AnyRef](implicit
      adapterEv: DateTimeAdapter[DateTime],
      eqEv: Equal[DateTime]
  ): DateTimeAdapterLaws[DateTime] =
    new DateTimeAdapterLaws[DateTime] {
      implicit val eq: Equal[DateTime] = eqEv
      implicit def adapter: DateTimeAdapter[DateTime] = adapterEv
    }

}
