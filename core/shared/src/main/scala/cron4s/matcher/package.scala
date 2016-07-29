package cron4s

/**
  * Created by domingueza on 29/07/2016.
  */
package object matcher {

  type FieldExtractor[A] = (CronField, A) => Option[Int]

}
