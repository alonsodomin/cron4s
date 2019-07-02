package cron4s
package expr2

private[expr2] trait HasCronUnit[F <: CronField] {
  def unit: CronUnit[F]
}
