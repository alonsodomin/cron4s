package cron4s.types

/**
  * Created by alonsodomin on 29/07/2016.
  */
trait Contravariant[F[_]] extends Serializable {

  def contramap[A, B](fa: F[A])(f: B => A): F[B]

}

object Contravariant {
  @inline def apply[F[_]](implicit F: Contravariant[F]): Contravariant[F] = F
}
