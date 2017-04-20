/*
 * Copyright 2017 Antonio Alonso Dominguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cron4s.testkit.gen

import cron4s.{CronField, CronUnit}
import cron4s.expr._
import cron4s.base._

import org.scalacheck._

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait NodeGenerators extends ArbitraryCronUnits with NodeConversions {

  private[this] def filterImpliedElems[F <: CronField](xs: List[EnumerableNode[F]]): List[EnumerableNode[F]] = {
    xs.foldRight(List.empty[EnumerableNode[F]]) { (node, result) =>
      val alreadyImplied = result.exists(_.impliedBy(node))
      val impliedByOther = result.exists(x => node.impliedBy(x))

      if (!(alreadyImplied || impliedByOther)) node :: result
      else result
    }
  }

  def anyGen[F <: CronField](implicit unit: CronUnit[F]): Gen[AnyNode[F]] =
    Gen.const(AnyNode[F])

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
  ): Gen[ConstNode[F]] =
    Gen.choose(unit.max + 1, Int.MaxValue).map(ConstNode[F](_))

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

  def enumerableGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[EnumerableNode[F]] = Gen.oneOf(
    constGen[F].map(const2Enumerable),
    betweenGen[F].map(between2Enumerable)
  )

  def invalidEnumerableGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[EnumerableNode[F]] = Gen.oneOf(
    Gen.oneOf(constGen[F], invalidConstGen[F]).map(const2Enumerable),
    Gen.oneOf(betweenGen[F], invalidBetweenGen[F]).map(between2Enumerable)
  )

  private[this] def severalGen0[F <: CronField](memberGen: Gen[EnumerableNode[F]])(
    inspectElements: List[EnumerableNode[F]] => List[EnumerableNode[F]]
  )(
    implicit unit: CronUnit[F]
  ): Gen[SeveralNode[F]] = {
    Gen.choose(1, 5)
      .flatMap(size => Gen.listOfN(size, memberGen))
      .map(inspectElements)
      .map { elems =>
        SeveralNode[F](elems.head, elems.tail: _*)
      }
  }

  def severalGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[SeveralNode[F]] = severalGen0(enumerableGen[F])(filterImpliedElems)

  def invalidSeveralGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[SeveralNode[F]] = severalGen0(invalidEnumerableGen[F])(identity)

  def divisibleGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[DivisibleNode[F]] = Gen.oneOf(
    eachGen[F].map(each2Divisible),
    betweenGen[F].map(between2Divisible),
    severalGen[F].map(several2Divisible)
  )

  private[this] def everyGen0[F <: CronField](
      baseGen: Gen[DivisibleNode[F]]
  )(implicit unit: CronUnit[F]): Gen[EveryNode[F]] = for {
    base <- baseGen
    freq <- Gen.posNum[Int]
  } yield EveryNode(base, freq)

  def everyGen[F <: CronField](
      implicit
      unit: CronUnit[F],
      ev: Enumerated[CronUnit[F]]
  ): Gen[EveryNode[F]] =
    everyGen0(divisibleGen[F])

  def nodeGen[F <: CronField](implicit unit: CronUnit[F], ev: Enumerated[CronUnit[F]]): Gen[FieldNode[F]] =
    Gen.oneOf(
      eachGen[F].map(each2Field),
      constGen[F].map(const2Field),
      betweenGen[F].map(between2Field),
      severalGen[F].map(several2Field),
      everyGen[F].map(every2Field)
    )

  def nodeWithAnyGen[F <: CronField](implicit unit: CronUnit[F], ev: Enumerated[CronUnit[F]]): Gen[FieldNodeWithAny[F]] =
    Gen.oneOf(
      anyGen[F].map(any2FieldWithAny),
      eachGen[F].map(each2FieldWithAny),
      constGen[F].map(const2FieldWithAny),
      betweenGen[F].map(between2FieldWithAny),
      severalGen[F].map(several2FieldWithAny),
      everyGen[F].map(every2FieldWithAny)
    )

}
