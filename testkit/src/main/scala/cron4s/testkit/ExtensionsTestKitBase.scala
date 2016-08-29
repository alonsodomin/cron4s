package cron4s.testkit

/**
  * Created by alonsodomin on 29/08/2016.
  */
trait ExtensionsTestKitBase[DateTime <: AnyRef] {

  protected def createDateTime(seconds: Int, minutes: Int, hours: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): DateTime

}
