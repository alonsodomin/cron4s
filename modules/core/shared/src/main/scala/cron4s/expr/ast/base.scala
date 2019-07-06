package cron4s
package expr
package ast

private[ast] trait HasCronUnit[F <: CronField] {
  def unit: CronUnit[F]
}
