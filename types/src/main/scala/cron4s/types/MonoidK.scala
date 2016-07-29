package cron4s.types

/**
  * Created by alonsodomin on 29/07/2016.
  */
trait MonoidK[F[_]] extends SemigroupK[F] {

  def empty[A]: F[A]

}

object MonoidK {
  @inline def apply[F[_]](implicit F: MonoidK[F]): MonoidK[F] = F
}
