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
trait ExprGens extends ArbitraryCronUnits {
  import Arbitrary._

  private[this] def filterImpliedElems[F <: CronField](xs: Vector[EnumExprAST[F]]): Vector[EnumExprAST[F]] = {
    val result = ListBuffer.empty[EnumExprAST[F]]
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

  def eachExprGen[F <: CronField](unit: CronUnit[F]): Gen[EachExpr[F]] =
    Gen.const(EachExpr()(unit))

  def constExpr[F <: CronField](unit: CronUnit[F], value: Int) =
    ConstExpr[F](value)(unit)

  def constExprGen[F <: CronField](unit: CronUnit[F])(
      implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[ConstExpr[F]] = for {
    value <- Gen.choose(unit.min, unit.max)
  } yield constExpr(unit, value)

  def invalidConstExprGen[F <: CronField](unit: CronUnit[F])(
      implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[ConstExpr[F]] = for {
    value <- arbitrary[Int]
    if (value < unit.min) || (value > unit.max)
  } yield constExpr(unit, value)

  def betweenExprGen[F <: CronField](unit: CronUnit[F])(
      implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[BetweenExpr[F]] = for {
    min  <- Gen.choose(unit.min, unit.max - 1)
    max  <- Gen.choose(min + 1, unit.max)
  } yield BetweenExpr(constExpr(unit, min), constExpr(unit, max))(unit)

  def enumerableExprGen[F <: CronField](unit: CronUnit[F])(
      implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[EnumExprAST[F]] = Gen.oneOf(
    constExprGen[F](unit).map(e => Coproduct[EnumExprAST[F]](e)),
    betweenExprGen[F](unit).map(e => Coproduct[EnumExprAST[F]](e))
  )

  def severalExpr[F <: CronField](unit: CronUnit[F], values: Vector[EnumExprAST[F]]): SeveralExpr[F] =
    SeveralExpr[F](NonEmptyList(values.head, values.tail: _*))(unit)

  def severalExprGen[F <: CronField](unit: CronUnit[F])(
      implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[SeveralExpr[F]] = for {
    size  <- Gen.posNum[Int] if size > 1
    elems <- Gen.containerOfN[Vector, EnumExprAST[F]](size, enumerableExprGen(unit))
  } yield severalExpr(unit, filterImpliedElems(elems))

  def divisibleExprGen[F <: CronField](unit: CronUnit[F])(
      implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[DivExprAST[F]] =
    Gen.oneOf(
      betweenExprGen[F](unit).map(e => Coproduct[DivExprAST[F]](e)),
      severalExprGen[F](unit).map(e => Coproduct[DivExprAST[F]](e))
    )

  def everyExprGen[F <: CronField](unit: CronUnit[F])(
      implicit
      ev: HasCronField[CronUnit, F]
  ): Gen[EveryExpr[F]] = for {
    base <- divisibleExprGen(unit)
    freq <- Gen.posNum[Int] if freq > 1
  } yield EveryExpr(base, freq)(unit)

  def exprGen[F <: CronField](unit: CronUnit[F])(implicit ev: HasCronField[CronUnit, F]): Gen[Expr[F]] =
    Gen.oneOf(eachExprGen[F](unit), constExprGen[F](unit), severalExprGen[F](unit), everyExprGen[F](unit))

}
