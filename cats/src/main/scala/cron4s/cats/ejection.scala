package cron4s.cats

import cats.Eval
import cron4s.types._

trait EjectedInstances extends EjectedInstances7

private[cats] trait EjectedInstances7 extends EjectedInstances5 {

  implicit def ejectEqual[A](implicit ev: Equal[A]): _root_.cats.Eq[A] =
    new EjectedEqual[A] { override val E = ev }

  private[cats] trait EjectedEqual[A] extends _root_.cats.Eq[A] {
    val E: Equal[A]
    override def eqv(x: A, y: A): Boolean = E.eqv(x, y)
  }

}

/*private[cats] trait EjectedInstances6 extends EjectedInstances5 {

  implicit def ejectTraverse[F[_]](implicit ev: Traverse[F]): _root_.cats.Traverse[F] =
    new EjectedTraverse[F] { override val F = ev }

  private[cats] trait EjectedTraverse[F[_]] extends EjectedFunctor[F] with EjectedFoldable[F] with _root_.cats.Traverse[F] {
    val F: Traverse[F]

    override def traverse[G[_]: _root_.cats.Applicative, A, B](fa: F[A])(f: A => G[B]): G[F[B]] =
      F.traverse(fa)(f)
  }
}*/

private[cats] trait EjectedInstances5 extends EjectedInstances4 {

  implicit def ejectFoldable[F[_]](implicit ev: Foldable[F]): _root_.cats.Foldable[F] =
    new EjectedFoldable[F] { override val F = ev }

  private[cats] trait EjectedFoldable[F[_]] extends _root_.cats.Foldable[F] {
    val F: Foldable[F]

    override def foldLeft[A, B](fa: F[A], b: B)(f: (B, A) => B): B = F.foldLeft(fa, b)(f)

    override def foldRight[A, B](fa: F[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
      lb.map(bz => F.foldRight(fa, bz)((a, b) => f(a, Eval.later(b)).value))
  }
}

private[cats] trait EjectedInstances4 extends EjectedInstances3 {

  implicit def ejectMonoidK[F[_]](implicit ev: MonoidK[F]): _root_.cats.MonoidK[F] =
    new EjectedMonoidK[F] { override val F = ev }

  private[cats] trait EjectedMonoidK[F[_]] extends EjectedSemigroupK[F] with _root_.cats.MonoidK[F] {
    val F: MonoidK[F]
    override def empty[A]: F[A] = F.empty[A]
  }
}

private[cats] trait EjectedInstances3 extends EjectedInstances2 {

  implicit def ejectSemigroupK[F[_]](implicit ev: SemigroupK[F]): _root_.cats.SemigroupK[F] =
    new EjectedSemigroupK[F] { override val F = ev }

  private[cats] trait EjectedSemigroupK[F[_]] extends _root_.cats.SemigroupK[F] {
    val F: SemigroupK[F]
    override def combineK[A](x: F[A], y: F[A]): F[A] = F.combineK(x, y)
  }
}

private[cats] trait EjectedInstances2 extends EjectedInstances1 {

  implicit def ejectedApplicative[F[_]](implicit ev: Applicative[F]): _root_.cats.Applicative[F] =
    new EjectedApplicative[F] { override val F = ev }

  private[cats] trait EjectedApplicative[F[_]] extends EjectedFunctor[F] with _root_.cats.Applicative[F] {
    val F: Applicative[F]
    override def pure[A](a: A): F[A] = F.pure(a)
    override def ap[A, B](ff: F[A => B])(fa: F[A]): F[B] = F.ap(ff)(fa)
  }
}

private[cats] trait EjectedInstances1 extends EjectedInstances0 {

  implicit def ejectFunctor[F[_]](implicit ev: Functor[F]): _root_.cats.Functor[F] =
    new EjectedFunctor[F] { override val F = ev }

  private[cats] trait EjectedFunctor[F[_]] extends _root_.cats.Functor[F] {
    val F: Functor[F]
    override def map[A, B](fa: F[A])(f: A => B): F[B] = F.map(fa)(f)
  }
}

private[cats] trait EjectedInstances0 {

  implicit def ejectContravariant[F[_]](implicit ev: Contravariant[F]): _root_.cats.functor.Contravariant[F] =
    new EjectedContravariant[F] { override val F = ev }

  private[cats] trait EjectedContravariant[F[_]] extends _root_.cats.functor.Contravariant[F] {
    val F: Contravariant[F]
    override def contramap[A, B](fa: F[A])(f: B => A): F[B] = F.contramap(fa)(f)
  }
}