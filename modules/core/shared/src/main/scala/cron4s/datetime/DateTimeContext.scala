package cron4s
package datetime

import cron4s.base._

trait DateTimeContext[DateTime] {
  def first(dt: DateTime, field: CronField): Either[DateTimeError, Int]
  def last(dt: DateTime, field: CronField): Either[DateTimeError, Int]
}
