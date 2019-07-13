package cron4s
package internal.base

import shapeless._

trait CronFieldExtractor[A] {
  type Out

  def extractField(a: A): Out
}

object CronFieldExtractor extends CronFieldExtractorDerivation {
  type Aux[A, Out0] = CronFieldExtractor[A] { type Out = Out0 }

  implicit def cronFieldExtractorFromSupportsCronField[A, F <: CronField](
    implicit supportsA: SupportsCronField[A, F]
  ): CronFieldExtractor[A] =
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
  ): CronFieldExtractor[A] = new CronFieldExtractor[A] {
    type Out = extractL.Out

    def extractField(a: A): Out = extractL.extractField(G.to(a))
  }
}

trait CronFieldExtractorDerivation1 extends CronFieldExtractorDerivation0 {
  implicit val cronFieldExtractorFromHNil: CronFieldExtractor[HNil] = new CronFieldExtractor[HNil] {
    type Out = HNil

    def extractField(a: HNil): Out = HNil
  }
}

trait CronFieldExtractorDerivation0 {
  implicit def cronFieldExtractorFromHList[H, T <: HList, F <: CronField, TF <: HList](
    implicit
    extractH: CronFieldExtractor.Aux[H, F],
    extractT: CronFieldExtractor.Aux[T, TF]
  ): CronFieldExtractor[H :: T] = new CronFieldExtractor[H :: T] {
    type Out = F :: TF

    def extractField(ht: H :: T): Out =
      extractH.extractField(ht.head) :: extractT.extractField(ht.tail)
  }
}