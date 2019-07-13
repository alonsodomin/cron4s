package cron4s
package datetime

import cron4s.internal.base._

trait DateTimeContext[DateTime] {
  def lowerBound[F <: CronField](dt: DateTime, field: F)(implicit ev: SupportsCronField[DateTime, F]): Int
  def upperBound[F <: CronField](dt: DateTime, field: F)(implicit ev: SupportsCronField[DateTime, F]): Int
  
  def get[F <: CronField](dt: DateTime, field: F)(implicit ev: SupportsCronField[DateTime, F]): Int
  def set[F <: CronField](dt: DateTime, field: F, value: Int)(implicit ev: SupportsCronField[DateTime, F]): Either[DateTimeError, DateTime]
}
