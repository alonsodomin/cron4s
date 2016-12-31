package cron4s.testkit.gen

import cron4s.{CronField, CronUnit}
import cron4s.expr._
import cron4s.types._
import cron4s.syntax._

import shapeless._

import org.scalacheck._

import scalaz.NonEmptyList

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait NodeGenerators extends ArbitraryCronUnits {
  import Arbitrary._

  private[this] def filterImpliedElems[F <: CronField](xs: List[SeveralMemberNode[F]]): List[SeveralMemberNode[F]] = {
    xs.foldRight(List.empty[SeveralMemberNode[F]]) { (node, result) =>
      val alreadyImplied = result.exists(_.impliedBy(node))
      val impliedByOther = result.exists(x => node.impliedBy(x))

      if (!(alreadyImplied || impliedByOther)) node :: result
      else result
    }
  }

  def eachGen[F <: CronField](implicit unit: CronUnit[F]): Gen[EachNode[F]] =
    Gen.const(EachNode[F])

  def constGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[ConstNode[F]] = for {
    value <- Gen.choose(unit.min, unit.max)
  } yield ConstNode(value)

  def invalidConstGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[ConstNode[F]] = for {
    value <- arbitrary[Int]
    if (value >= 0) && (value < unit.min) || (value > unit.max)
  } yield ConstNode(value)

  def betweenGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[BetweenNode[F]] = for {
    min  <- Gen.choose(unit.min, unit.max - 1)
    max  <- Gen.choose(min + 1, unit.max)
  } yield BetweenNode(ConstNode(min), ConstNode(max))

  def invalidBetweenGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[BetweenNode[F]] = {
    for {
      min <- Gen.oneOf(constGen[F], invalidConstGen[F])
      max <- Gen.oneOf(constGen[F], invalidConstGen[F])
    } yield BetweenNode(min, max)
  }

  def severalMemberGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[SeveralMemberNode[F]] = Gen.oneOf(
    constGen[F].map(Coproduct[SeveralMemberNode[F]](_)),
    betweenGen[F].map(Coproduct[SeveralMemberNode[F]](_))
  )

  def invalidSeveralMemberGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[SeveralMemberNode[F]] = Gen.oneOf(
    Gen.oneOf(constGen[F], invalidConstGen[F]).map(Coproduct[SeveralMemberNode[F]](_)),
    Gen.oneOf(betweenGen[F], invalidBetweenGen[F]).map(Coproduct[SeveralMemberNode[F]](_))
  )

  private[this] def severalGen0[F <: CronField](memberGen: Gen[SeveralMemberNode[F]])(
    inspectElements: List[SeveralMemberNode[F]] => List[SeveralMemberNode[F]]
  )(
    implicit
    unit: CronUnit[F],
    ev: Enumerated[CronUnit[F]]
  ): Gen[SeveralNode[F]] = {
    Gen.choose(1, 5)
      .flatMap(size => Gen.listOfN(size, memberGen))
      .map(inspectElements)
      .map { elems =>
        SeveralNode[F](NonEmptyList(elems.head, elems.tail: _*))
      }
  }

  def severalGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[SeveralNode[F]] = severalGen0(severalMemberGen[F])(filterImpliedElems)

  def invalidSeveralGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[SeveralNode[F]] = severalGen0(invalidSeveralMemberGen[F])(identity)

  def frequencyBaseGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[FrequencyBaseNode[F]] = Gen.oneOf(
    eachGen[F].map(Coproduct[FrequencyBaseNode[F]](_)),
    betweenGen[F].map(Coproduct[FrequencyBaseNode[F]](_)),
    severalGen[F].map(Coproduct[FrequencyBaseNode[F]](_))
  )

  def invalidFrequencyBaseGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[FrequencyBaseNode[F]] = Gen.oneOf(
    eachGen[F].map(Coproduct[FrequencyBaseNode[F]](_)),
    Gen.oneOf(betweenGen[F], invalidBetweenGen[F]).map(Coproduct[FrequencyBaseNode[F]](_)),
    Gen.oneOf(severalGen[F], invalidSeveralGen[F]).map(Coproduct[FrequencyBaseNode[F]](_))
  )

  private[this] def everyGen0[F <: CronField](
      baseGen: Gen[FrequencyBaseNode[F]]
  )(
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[EveryNode[F]] = for {
    base <- baseGen
    freq <- Gen.posNum[Int]
  } yield EveryNode(base, freq)

  def everyGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[EveryNode[F]] =
    everyGen0(frequencyBaseGen[F])

  def invalidEveryGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[EveryNode[F]] =
    everyGen0(invalidFrequencyBaseGen[F])

  def nodeGen[F <: CronField](implicit unit: CronUnit[F], ev: Enumerated[CronUnit[F]]): Gen[Node[F]] =
    Gen.oneOf(eachGen[F], constGen[F], severalGen[F], everyGen[F])

}
