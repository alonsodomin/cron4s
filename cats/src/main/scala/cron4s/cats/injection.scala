package cron4s.cats

import cats.{Eq, Eval, Foldable, Functor, MonoidK, SemigroupK}
import cats.functor.Contravariant

trait InjectionInstances extends InjectionInstances5

private[cats] trait InjectionInstances6 extends InjectionInstances5 {

  implicit def injectEq[A](implicit ev: Eq[A]): cron4s.types.Equal[A] =
    new InjectedEq[A] { override val E: Eq[A] = ev }

  private[cats] trait InjectedEq[A] extends cron4s.types.Equal[A] {
    val E: Eq[A]
    override def eqv(x: A, y: A): Boolean = E.eqv(x, y)
  }
}

private[cats] trait InjectionInstances5 extends InjectionInstances4 {
  private[cats] trait InjectedTraverse[F[_]] extends cron4s.types.Traverse[F] {

  }
}

private[cats] trait InjectionInstances4 extends InjectionInstances3 {

  implicit def injectFoldable[F[_]](implicit ev: Foldable[F]): cron4s.types.Foldable[F] =
    new InjectedFoldable[F] { override val F = ev }

  private[cats] trait InjectedFoldable[F[_]] extends cron4s.types.Foldable[F] {
    val F: Foldable[F]

    def foldLeft[A, B](fa: F[A], b: B)(f: (B, A) => B): B = F.foldLeft(fa, b)(f)

    def foldRight[A, B](fa: F[A], b: => B)(f: (A, => B) => B): B =
      F.foldRight(fa, Eval.later(b))((a, eval) => eval.map(b => f(a, b))).value
  }
}

private[cats] trait InjectionInstances3 extends InjectionInstances2 {

  implicit def injectMonoidK[F[_]](implicit ev: MonoidK[F]): cron4s.types.SemigroupK[F] =
    new InjectedMonoidK[F] { override val F: MonoidK[F] = ev }

  private[cats] trait InjectedMonoidK[F[_]] extends cron4s.types.MonoidK[F] with InjectedSemigroupK[F] {
    val F: MonoidK[F]
    def empty[A]: F[A] = F.empty[A]
  }
}

private[cats] trait InjectionInstances2 extends InjectionInstances1 {

  implicit def injectSemigroupK[F[_]](implicit ev: SemigroupK[F]): cron4s.types.SemigroupK[F] =
    new InjectedSemigroupK[F] { override val F: SemigroupK[F] = ev }

  private[cats] trait InjectedSemigroupK[F[_]] extends cron4s.types.SemigroupK[F] {
    val F: SemigroupK[F]
    def combineK[A](x: F[A], y: F[A]): F[A] = F.combineK(x, y)
  }
}

private[cats] trait InjectionInstances1 extends InjectionInstances0 {

  implicit def injectFunctor[F[_]](implicit ev: Functor[F]): cron4s.types.Functor[F] =
    new InjectedFunctor[F] { override val F = ev }

  private[cats] trait InjectedFunctor[F[_]] extends cron4s.types.Functor[F] {
    val F: Functor[F]
    def map[A, B](fa: F[A])(f: A => B) = F.map(fa)(f)
  }
}

private[cats] trait InjectionInstances0 {
  implicit def injectContravariant[F[_]](implicit ev: Contravariant[F]): cron4s.types.Contravariant[F] =
    new cron4s.types.Contravariant[F] {
      override def contramap[A, B](fa: F[A])(f: (B) => A): F[B] = ev.contramap(fa)(f)
    }
}