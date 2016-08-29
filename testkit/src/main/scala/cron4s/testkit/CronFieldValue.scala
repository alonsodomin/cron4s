package cron4s.testkit

import cron4s.CronField

/**
  * Created by alonsodomin on 29/08/2016.
  */
case class CronFieldValue[F <: CronField](field: F, value: Int)
