package cron4s

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait IsCronUnit[U] {
  type F <: CronField

  def apply(unit: U): CronUnit[F]
}

object IsCronUnit {
  @inline def apply[U](implicit ev: IsCronUnit[U]) = ev

  implicit def mk[F0 <: CronField] = new IsCronUnit[CronUnit[F0]] {
    type F = F0

    @inline def apply(unit: CronUnit[F0]): CronUnit[F0] = unit
  }
}
