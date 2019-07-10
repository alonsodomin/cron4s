package cron4s
package base

trait HasCronUnit[T, F <: CronField] {
  def unit(t: T): CronUnit[F]
}

object HasCronUnit {
  def apply[T, F <: CronField](implicit ev: HasCronUnit[T, F]): HasCronUnit[T, F] = ev

  def instance[T, F <: CronField](f: T => CronUnit[F]): HasCronUnit[T, F] =
    new HasCronUnit[T, F] {
      def unit(t: T): CronUnit[F] = f(t)
    }
}
