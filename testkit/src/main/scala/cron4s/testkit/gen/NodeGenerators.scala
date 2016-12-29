package cron4s.testkit.gen

import cron4s.expr._
import cron4s.types._
import cron4s.syntax._
import cron4s.{CronField, CronUnit}

import shapeless._

import org.scalacheck._

import scala.collection.mutable.ListBuffer
import scalaz.NonEmptyList

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait NodeGenerators extends ArbitraryCronUnits {
  import Arbitrary._

  private[this] def filterImpliedElems[F <: CronField](xs: Vector[SeveralMemberNode[F]]): Vector[SeveralMemberNode[F]] = {
    val result = ListBuffer.empty[SeveralMemberNode[F]]
    var idx = 0
    while (idx < xs.size) {
      val x = xs(idx)
      val alreadyImplied = result.find(_.impliedBy(x))
      if (alreadyImplied.isDefined) {
        result -= alreadyImplied.get
      }

      if (!result.exists(e => x.impliedBy(e))) {
        result += x
      }
      idx += 1
    }
    result.toVector
  }

  def eachGen[F <: CronField](implicit unit: CronUnit[F]): Gen[EachNode[F]] =
    Gen.const(EachNode[F])

  def constGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[ConstNode[F]] = for {
    value <- Gen.choose(unit.min, unit.max)
  } yield ConstNode(value)(unit)

  def invalidConstGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[ConstNode[F]] = for {
    value <- arbitrary[Int]
    if (value < unit.min) || (value > unit.max)
  } yield ConstNode(value)

  def betweenGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[BetweenNode[F]] = for {
    min  <- Gen.choose(unit.min, unit.max - 1)
    max  <- Gen.choose(min + 1, unit.max)
  } yield BetweenNode(ConstNode(min)(unit), ConstNode(max)(unit))(unit)

  def severalMemberGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[SeveralMemberNode[F]] = Gen.oneOf(
    constGen[F].map(e => Coproduct[SeveralMemberNode[F]](e)),
    betweenGen[F].map(e => Coproduct[SeveralMemberNode[F]](e))
  )

  def severalGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[SeveralNode[F]] = for {
    size  <- Gen.posNum[Int] if size > 1
    elems <- Gen.containerOfN[Vector, SeveralMemberNode[F]](size, severalMemberGen)
  } yield {
    val validElems = filterImpliedElems(elems)
    SeveralNode[F](NonEmptyList(validElems.head, validElems.tail: _*))(unit)
  }

  def frequencyBaseGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[FrequencyBaseNode[F]] =
    Gen.oneOf(
      betweenGen[F].map(e => Coproduct[FrequencyBaseNode[F]](e)),
      severalGen[F].map(e => Coproduct[FrequencyBaseNode[F]](e))
    )

  def everyGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[EveryNode[F]] = for {
    base <- frequencyBaseGen
    freq <- Gen.posNum[Int] if freq > 1
  } yield EveryNode(base, freq)(unit)

  def nodeGen[F <: CronField](implicit unit: CronUnit[F], ev: Enumerated[CronUnit[F]]): Gen[Node[F]] =
    Gen.oneOf(eachGen[F], constGen[F], severalGen[F], everyGen[F])

}
