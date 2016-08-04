package cron4s.expr

import cron4s._
import org.scalacheck._

import scala.collection.mutable.ListBuffer

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait ExprGenerators extends BaseGenerators {

  private[this] def filterImpliedElems[U](unit: U, xs: Vector[EnumerableExpr[_]])(implicit isUnit: IsCronUnit[U]): Vector[EnumerableExpr[isUnit.F]] = {
    val mapped = xs.map(_.asInstanceOf[EnumerableExpr[isUnit.F]])
    val result = ListBuffer.empty[EnumerableExpr[isUnit.F]]
    var idx = 0
    while (idx < mapped.size) {
      val x = mapped(idx)
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

  // Helper methods able to construct an expression from a `CronUnit` value

  protected def createAny[U](unit: U)(implicit isUnit: IsCronUnit[U]): AnyExpr[isUnit.F] =
    AnyExpr[isUnit.F]()(isUnit(unit))

  private[this] def createConst[U](unit: U, value: Int)(implicit isUnit: IsCronUnit[U]): ConstExpr[isUnit.F] =
    ConstExpr[isUnit.F](isUnit(unit).field, value)(isUnit(unit))

  private[this] def createBetween[U](unit: U, min: Int, max: Int)(implicit isUnit: IsCronUnit[U]): BetweenExpr[isUnit.F] =
    BetweenExpr[isUnit.F](createConst(isUnit(unit), min), createConst(isUnit(unit), max))(isUnit(unit))

  private[this] def createSeveral[U](unit: U, elems: Vector[EnumerableExpr[_]])(implicit isUnit: IsCronUnit[U]): SeveralExpr[isUnit.F] = {
    val mappedElems = elems.map(_.asInstanceOf[EnumerableExpr[isUnit.F]])
    new SeveralExpr[isUnit.F](mappedElems.sorted)(isUnit(unit))
  }

  private[this] def createEvery[U](unit: U, base: DivisibleExpr[_], freq: Int)(implicit isUnit: IsCronUnit[U]): EveryExpr[isUnit.F] = {
    val mappedBase = base.asInstanceOf[DivisibleExpr[isUnit.F]]
    EveryExpr[isUnit.F](mappedBase, freq)(isUnit(unit))
  }

  // Methods to construct expression generators for a give CronUnit. Uses same type resolution technique as above ones

  def constExpr[U](unit: U)(implicit isCronUnit: IsCronUnit[U]): Gen[ConstExpr[isCronUnit.F]] = {
    val resolved = isCronUnit(unit)
    for {
      value <- Gen.choose(resolved.min, resolved.max)
    } yield ConstExpr[isCronUnit.F](resolved.field, value)(resolved)
  }

  def betweenExpr[U](unit: U)(implicit isCronUnit: IsCronUnit[U]): Gen[BetweenExpr[isCronUnit.F]] = {
    val resolved = isCronUnit(unit)
    for {
      min  <- Gen.choose(resolved.min, resolved.max - 1)
      max  <- Gen.choose(min + 1, resolved.max)
    } yield BetweenExpr[isCronUnit.F](createConst(resolved, min), createConst(resolved, max))(resolved)
  }

  def enumerableExpressions[U](unit: U)(implicit isCronUnit: IsCronUnit[U]): Gen[EnumerableExpr[isCronUnit.F]] =
    Gen.oneOf[EnumerableExpr[isCronUnit.F]](constExpr(unit), betweenExpr(unit))

  def enumerableExpressionsVector[U](unit: U)(implicit isCronUnit: IsCronUnit[U]): Gen[Vector[EnumerableExpr[isCronUnit.F]]] = {
    val resolved = isCronUnit(unit)
    for {
      size  <- Gen.choose(5, 1500)
      elems <- Gen.containerOfN[Vector, EnumerableExpr[_ <: CronField]](size, enumerableExpressions(resolved))
    } yield filterImpliedElems(resolved, elems)
  }

  def severalExpr[U](unit: U)(implicit isCronUnit: IsCronUnit[U]): Gen[SeveralExpr[isCronUnit.F]] = {
    val resolved = isCronUnit(unit)
    for {
      elems <- enumerableExpressionsVector(resolved) retryUntil(_.size > 2)
    } yield createSeveral(resolved, elems)
  }

  def divisibleExpressions[U](unit: U)(implicit isCronUnit: IsCronUnit[U]): Gen[DivisibleExpr[isCronUnit.F]] =
    Gen.oneOf[DivisibleExpr[isCronUnit.F]](betweenExpr(unit), severalExpr(unit))

  // Random expression generators

  lazy val anyExpressions = for {
    unit <- cronUnits
  } yield createAny(unit)
  implicit lazy val arbitraryAnyExpression = Arbitrary(anyExpressions)

  lazy val constExpressions = for {
    unit  <- cronUnits
    value <- Gen.choose(unit.min, unit.max)
  } yield createConst(unit, value)
  implicit lazy val arbitraryConstExpression = Arbitrary(constExpressions)

  lazy val betweenExpressions = for {
    unit <- cronUnits
    min  <- Gen.choose(unit.min, unit.max - 1)
    max  <- Gen.choose(min + 1, unit.max)
  } yield createBetween(unit, min, max)
  implicit lazy val arbitraryBetweenExpression = Arbitrary(betweenExpressions)

  val severalExpressions = for {
    unit  <- cronUnits
    elems <- enumerableExpressionsVector(unit)
  } yield createSeveral(unit, elems)
  implicit lazy val arbitrarySeveralExpression = Arbitrary(severalExpressions)

  val everyExpressions = for {
    unit <- cronUnits
    base <- divisibleExpressions(unit)
    freq <- Gen.posNum[Int] if freq > 0
  } yield createEvery(unit, base, freq)
  implicit lazy val arbitraryEveryExpression = Arbitrary(everyExpressions)

}
