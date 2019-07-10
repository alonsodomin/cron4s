package cron4s
package base

trait HasCronUnit[T, F <: CronField] {
  def unit(t: T): F
}