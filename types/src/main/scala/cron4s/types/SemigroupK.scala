package cron4s.types

/**
  * Created by alonsodomin on 29/07/2016.
  */
trait SemigroupK[F[_]] extends Serializable {

  def combineK[A](x: F[A], y: F[A]): F[A]

}

object SemigroupK {
  @inline def apply[F[_]](implicit F: SemigroupK[F]): SemigroupK[F] = F
}
