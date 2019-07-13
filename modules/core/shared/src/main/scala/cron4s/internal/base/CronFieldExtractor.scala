package cron4s
package internal.base

import shapeless._

trait CronFieldExtractor[A] {
  type Out

  def extractField(a: A): Out
}

object CronFieldExtractor extends CronFieldExtractorDerivation {
  type Aux[A, Out0] = CronFieldExtractor[A] { type Out = Out0 }

  def apply[A](a: A)(implicit ev: CronFieldExtractor[A]): CronFieldExtractor[A] = ev

  def by[A, B](f : A => B)(implicit extractorB: CronFieldExtractor[B]): CronFieldExtractor[A] =
    new CronFieldExtractor[A] {
      type Out = extractorB.Out

      def extractField(a: A): Out = extractorB.extractField(f(a))
    }

  def const[A]: ConstPartiallyApplied[A] = new ConstPartiallyApplied[A]

  sealed class ConstPartiallyApplied[A] {
    def apply[B](a: A): CronFieldExtractor.Aux[B, A] = new CronFieldExtractor[B] {
      type Out = A
      @inline def extractField(b: B): A = a
    }
  }

  implicit def cronFieldExtractorFromSupportsCronField[A, F <: CronField](
    implicit supportsA: SupportsCronField[A, F]
  ): CronFieldExtractor.Aux[A, F] =
    new CronFieldExtractor[A] {
      type Out = F

      def extract(a: A) = supportsA.field(a)
    }

}

trait CronFieldExtractorDerivation extends CronFieldExtractorDerivation1 {
  implicit def cronFieldExtractorFromProduct[A, L <: HList](
    implicit
    G: Generic.Aux[A, L],
    extractL: CronFieldExtractor[L]
  ): CronFieldExtractor[A] = CronFieldExtractor.by(G.to)
}

trait CronFieldExtractorDerivation1 extends CronFieldExtractorDerivation0 {
  // implicit def cronFieldExtractorFromHList2[T <: HList, F <: CronField, TF <: HList](
  //   implicit
  //   extractT: Lazy[CronFieldExtractor.Aux[T, TF]]
  // ): CronFieldExtractor[F :: T] = new CronFieldExtractor[F :: T] {
  //   type Out = F :: TF

  //   def extractField(ht: F :: T): Out =
  //     ht.head :: extractT.value.extractField(ht.tail)
  // }

  implicit def cronFieldExtractorRefl[F <: CronField]: CronFieldExtractor[F] =
    new CronFieldExtractor[F] {
      type Out = F
      @inline def extractField(a: F): F = a
    }

  implicit def cronFieldExtractorFromHNil[L <: HNil]: CronFieldExtractor[L] = new CronFieldExtractor[L] {
    type Out = HNil

    def extractField(a: L): Out = HNil
  }

}

trait CronFieldExtractorDerivation0 {
  implicit def cronFieldExtractorFromHList[H, T <: HList, TF <: HList](
    implicit
    extractH: CronFieldExtractor[H],
    extractT: Lazy[CronFieldExtractor.Aux[T, TF]]
  ): CronFieldExtractor[H :: T] = new CronFieldExtractor[H :: T] {
    type Out = extractH.Out :: TF

    def extractField(ht: H :: T): Out =
      extractH.extractField(ht.head) :: extractT.value.extractField(ht.tail)
  }  

}