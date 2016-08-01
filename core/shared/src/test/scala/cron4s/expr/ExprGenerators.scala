package cron4s.expr

import cron4s._
import org.scalacheck._

import scala.collection.immutable.SortedSet

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait ExprGenerators extends BaseGenerators {

  private[this] def createAny[U](unit: U)(implicit isUnit: IsCronUnit[U]): AnyExpr[isUnit.F] =
    AnyExpr[isUnit.F]()(isUnit(unit))

  private[this] def createConst[U](unit: U, value: Int)(implicit isUnit: IsCronUnit[U]): ConstExpr[isUnit.F] =
    ConstExpr[isUnit.F](isUnit(unit).field, value)(isUnit(unit))

  private[this] def createBetween[U](unit: U, min: Int, max: Int)(implicit isUnit: IsCronUnit[U]): BetweenExpr[isUnit.F] =
    BetweenExpr[isUnit.F](createConst(isUnit(unit), min), createConst(isUnit(unit), max))(isUnit(unit))

  private[this] def createSeveral[U](unit: U, elems: Vector[EnumerableExpr[_]])(implicit isUnit: IsCronUnit[U]): SeveralExpr[isUnit.F] = {
    val mappedElems = elems.map(_.asInstanceOf[EnumerableExpr[isUnit.F]])
    SeveralExpr[isUnit.F](mappedElems.head, mappedElems.tail.toList)(isUnit(unit))
  }

  private[this] def createEvery[U](unit: U, base: DivisibleExpr[_], freq: Int)(implicit isUnit: IsCronUnit[U]): EveryExpr[isUnit.F] = {
    val mappedBase = base.asInstanceOf[DivisibleExpr[isUnit.F]]
    EveryExpr[isUnit.F](mappedBase, freq)(isUnit(unit))
  }

  lazy val anyExpressions = for {
    unit <- cronUnits
  } yield createAny(unit)

  def constExpr[U](unit: U)(implicit isCronUnit: IsCronUnit[U]): Gen[ConstExpr[isCronUnit.F]] = {
    val resolved = isCronUnit(unit)
    for {
      value <- Gen.choose(resolved.min, resolved.max)
    } yield ConstExpr[isCronUnit.F](resolved.field, value)(resolved)
  }

  lazy val constExpressions = for {
    unit  <- cronUnits
    value <- Gen.choose(unit.min, unit.max)
  } yield createConst(unit, value)
  implicit lazy val arbitraryConstExpression = Arbitrary(constExpressions)

  def betweenExpr[U](unit: U)(implicit isCronUnit: IsCronUnit[U]): Gen[BetweenExpr[isCronUnit.F]] = {
    val resolved = isCronUnit(unit)
    for {
      min  <- Gen.choose(resolved.min, resolved.max - 1)
      max  <- Gen.choose(min + 1, resolved.max)
    } yield BetweenExpr[isCronUnit.F](createConst(resolved, min), createConst(resolved, max))(resolved)
  }

  lazy val betweenExpressions = for {
    unit <- cronUnits
    min  <- Gen.choose(unit.min, unit.max - 1)
    max  <- Gen.choose(min + 1, unit.max)
  } yield createBetween(unit, min, max)
  implicit lazy val arbitraryBetweenExpression = Arbitrary(betweenExpressions)

  def enumerableExpressions[U](unit: U)(implicit isCronUnit: IsCronUnit[U]): Gen[EnumerableExpr[isCronUnit.F]] =
    Gen.oneOf[EnumerableExpr[isCronUnit.F]](constExpr(unit), betweenExpr(unit))

  val severalExpressions = for {
    unit  <- cronUnits
    size  <- Gen.posNum[Int] if size > 1
    elems <- Gen.containerOfN[Vector, EnumerableExpr[_ <: CronField]](size, enumerableExpressions(unit))
  } yield createSeveral(unit, elems)

  def severalExpr[U](unit: U)(implicit isCronUnit: IsCronUnit[U]): Gen[SeveralExpr[isCronUnit.F]] = {
    val resolved = isCronUnit(unit)
    for {
      size  <- Gen.posNum[Int] if size > 1
      elems <- Gen.containerOfN[Vector, EnumerableExpr[_ <: CronField]](size, enumerableExpressions(resolved))
    } yield createSeveral(resolved, elems)
  }

  def divisibleExpressions[U](unit: U)(implicit isCronUnit: IsCronUnit[U]): Gen[DivisibleExpr[isCronUnit.F]] =
    Gen.oneOf[DivisibleExpr[isCronUnit.F]](betweenExpr(unit), severalExpr(unit))

  val everyExpressions = for {
    unit <- cronUnits
    base <- divisibleExpressions(unit)
    freq <- Gen.posNum[Int] if freq > 0
  } yield createEvery(unit, base, freq)

}
