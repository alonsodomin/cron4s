package cron4s

import cron4s.expr.CronField

/**
  * Created by domingueza on 29/07/2016.
  */
package object matcher {

  type FieldExtractor[A] = (CronField, A) => Option[Int]

}
