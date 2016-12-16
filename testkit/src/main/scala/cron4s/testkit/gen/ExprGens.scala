package cron4s.testkit.gen

import cron4s.expr._
import cron4s.types._
import cron4s.types.syntax._
import cron4s.{CronField, CronUnit}

import org.scalacheck._

import scala.collection.mutable.ListBuffer
import scalaz.NonEmptyList

/**
  * Created by alonsodomin on 28/08/2016.
  */
trait ExprGens extends ArbitraryCronUnits {

  private[this] def filterImpliedElems[F <: CronField](xs: Vector[EnumerableExpr[F]]): Vector[EnumerableExpr[F]] = {
    val result = ListBuffer.empty[EnumerableExpr[F]]
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

  def anyExprGen[F <: CronField](unit: CronUnit[F]): Gen[AnyExpr[F]] =
    Gen.const(AnyExpr()(unit))

  def constExpr[F <: CronField](unit: CronUnit[F], value: Int)(implicit ev: HasCronField[CronUnit, F]) =
    ConstExpr[F](value)(unit, ev, IsFieldExpr[EnumerableExpr, F])

  def constExprGen[F <: CronField](unit: CronUnit[F])(implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[ConstExpr[F]] = for {
    value <- Gen.choose(unit.min, unit.max)
  } yield constExpr(unit, value)

  def betweenExprGen[F <: CronField](unit: CronUnit[F])(implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[BetweenExpr[F]] = for {
    min  <- Gen.choose(unit.min, unit.max - 1)
    max  <- Gen.choose(min + 1, unit.max)
  } yield BetweenExpr(constExpr(unit, min), constExpr(unit, max))(unit, IsFieldExpr[EnumerableExpr, F])

  def enumerableExprGen[F <: CronField](unit: CronUnit[F])(implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[EnumerableExpr[F]] = Gen.oneOf(constExprGen[F](unit), betweenExprGen[F](unit))

  def severalExpr[F <: CronField](unit: CronUnit[F], values: Vector[EnumerableExpr[F]]): SeveralExpr[F] = {
    val sortedValues = values.sorted
    SeveralExpr[F](NonEmptyList(sortedValues.head, sortedValues.tail: _*))(unit)
  }

  def severalExprGen[F <: CronField](unit: CronUnit[F])(implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[SeveralExpr[F]] = for {
    size  <- Gen.posNum[Int] if size > 1
    elems <- Gen.containerOfN[Vector, EnumerableExpr[F]](size, enumerableExprGen(unit))
  } yield severalExpr(unit, filterImpliedElems(elems))

  def divisibleExprGen[F <: CronField](unit: CronUnit[F])(implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[DivisibleExpr[F]] =
    Gen.oneOf(betweenExprGen[F](unit), severalExprGen[F](unit))

  def everyExprGen[F <: CronField](unit: CronUnit[F])(implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[EveryExpr[F]] = for {
    base <- Gen.oneOf(betweenExprGen[F](unit), severalExprGen[F](unit))
    freq <- Gen.posNum[Int] if freq > 1
  } yield EveryExpr(base, freq)(unit, IsFieldExpr[DivisibleExpr, F])

  def exprGen[F <: CronField](unit: CronUnit[F])(implicit ev: HasCronField[CronUnit, F]): Gen[Expr[F]] =
    Gen.oneOf(anyExprGen[F](unit), constExprGen[F](unit), severalExprGen[F](unit), everyExprGen[F](unit))

}
