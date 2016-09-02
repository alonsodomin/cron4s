package cron4s.expr

import cron4s.{CronField, CronUnit}
import cron4s.types._
import cron4s.types.syntax._
import cron4s.validation._

import scala.language.{implicitConversions, higherKinds}
import scala.util.parsing.input.Positional

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 07/11/2015.
  */
sealed trait Expr[F <: CronField] extends Positional {
  final type FieldType = F

  val unit: CronUnit[F]

  val range: IndexedSeq[Int]

}

sealed trait DivisibleExpr[F <: CronField] extends Expr[F]
sealed trait EnumerableExpr[F <: CronField] extends Expr[F] with Ordered[EnumerableExpr[F]]

object Expr extends ExprInstances

sealed trait SpecialChar

final case class AnyExpr[F <: CronField](implicit val unit: CronUnit[F])
  extends Expr[F] with DivisibleExpr[F] with SpecialChar {

  val range = unit.range

  override def toString = "*"

}

case object Last extends SpecialChar

final case class ConstExpr[F <: CronField]
    (field: F, value: Int, textValue: Option[String] = None)
    (implicit val unit: CronUnit[F], ev: HasCronField[CronUnit, F], ops: IsFieldExpr[EnumerableExpr, F])
  extends Expr[F] with DivisibleExpr[F] with EnumerableExpr[F] {

  require(value >= unit.min && value <= unit.max, s"Value $value is out of bounds for field: ${unit.field}")

  override def compare(that: EnumerableExpr[F]): Int = {
    if (value > ops.min(that)) 1
    else if (value < ops.max(that)) -1
    else 0
  }

  val range = Vector(value)

  override def toString = textValue.getOrElse(value.toString)

}

final case class BetweenExpr[F <: CronField]
    (begin: ConstExpr[F], end: ConstExpr[F])
    (implicit val unit: CronUnit[F], ops: IsFieldExpr[EnumerableExpr, F])
  extends Expr[F] with DivisibleExpr[F] with EnumerableExpr[F] {

  require(begin.value < end.value, s"$begin should be less than $end")

  override def compare(that: EnumerableExpr[F]): Int = {
    if (ops.min(this) > ops.max(that)) 1
    else if (ops.max(this) < ops.min(that)) -1
    else 0
  }

  val range = begin.value to end.value

  override def toString = s"$begin-$end"

}

final case class SeveralExpr[F <: CronField] private[expr]
    (values: Vector[EnumerableExpr[F]])
    (implicit val unit: CronUnit[F])
  extends Expr[F] with DivisibleExpr[F] {

  require(values.nonEmpty, "Expression should contain at least one element")

  val range: IndexedSeq[Int] = values.flatMap(_.range).distinct.sorted

  override def toString = values.mkString(",")

}
object SeveralExpr {

  def apply[F <: CronField]
      (elements: NonEmptyList[EnumerableExpr[F]])
      (implicit unit: CronUnit[F], ops: IsFieldExpr[EnumerableExpr, F]
  ): ValidatedExpr[SeveralExpr, F] =
    validateSeveral[F](elements)

}

final case class EveryExpr[F <: CronField]
    (value: DivisibleExpr[F], freq: Int)
    (implicit val unit: CronUnit[F], ops: IsFieldExpr[DivisibleExpr, F])
  extends Expr[F] {

  val range: Vector[Int] = {
    val elements = Stream.iterate[Option[(Int, Int)]](Some(ops.min(value) -> 0)) {
      prev => prev.flatMap { case (v, _) => ops.step(value)(v, freq) }
    }.flatten.takeWhile(_._2 < 1).map(_._1)

    elements.toVector
  }

  override def toString = s"$value/$freq"

}

private[expr] trait ExprInstances extends ExprInstances1 {

  implicit def anyExprLike[F <: CronField]: IsFieldExpr[AnyExpr, F] =
    new IsFieldExprBase[AnyExpr, F] {
      override def matches(e: AnyExpr[F]): Predicate[Int] = Predicate { x =>
        x >= min(e) && x <= max(e)
      }
    }

  implicit def constExprLike[F <: CronField]: IsFieldExpr[ConstExpr, F] =
    new IsFieldExprBase[ConstExpr, F] {
      override def matches(e: ConstExpr[F]): Predicate[Int] = equalTo(e.value)

      /*override def compare(x: ConstExpr[F], y: ConstExpr[F]): Int = {
        if (x.value < min(y)) 1
        else if (x.value > max(y)) -1
        else 0
      }*/
    }

  implicit def betweenExprLike[F <: CronField]: IsFieldExpr[BetweenExpr, F] =
    new IsFieldExprBase[BetweenExpr, F] {

      override def matches(e: BetweenExpr[F]): Predicate[Int] = Predicate { x =>
        x >= e.begin.value && x <= e.end.value
      }

      /*override def compare(x: BetweenExpr[F], y: BetweenExpr[F]): Int = {
        if (min(x) > max(y)) 1
        else if (max(x) < min(y)) -1
        else 0
      }*/
    }

  implicit def severalExprLike[F <: CronField]
    (implicit ev: IsFieldExpr[EnumerableExpr, F]): IsFieldExpr[SeveralExpr, F] =
      new IsFieldExprBase[SeveralExpr, F] {
        override def matches(e: SeveralExpr[F]): Predicate[Int] =
          anyOf(e.values.map(ev.matches))
      }

  implicit def everyExprLike[F <: CronField]: IsFieldExpr[EveryExpr, F] =
    new IsFieldExprBase[EveryExpr, F] {

      override def matches(e: EveryExpr[F]): Predicate[Int] =
        anyOf(e.range.map(x => equalTo(x)))

      override def steppingUnit(a: EveryExpr[F]): Int = a.freq

    }

}

private[expr] trait ExprInstances1 extends ExprInstances0 {

  implicit def divisibleExpr[F <: CronField]: IsFieldExpr[DivisibleExpr, F] =
    new IsFieldExprBase[DivisibleExpr, F] {}

  implicit def enumerableExpr[F <: CronField]: IsFieldExpr[EnumerableExpr, F] =
    new IsFieldExprBase[EnumerableExpr, F] {}

}

private[expr] trait ExprInstances0 {
  private[expr] trait IsFieldExprBase[E[_ <: CronField] <: Expr[_], F <: CronField] extends IsFieldExpr[E, F] {
    override def matches(e: E[F]): Predicate[Int] =
      Predicate { x => e.range.contains(x) }

    override def range(expr: E[F]): IndexedSeq[Int] = expr.range
  }

  implicit def baseExpr[F <: CronField]: IsFieldExpr[Expr, F] =
    new IsFieldExprBase[Expr, F] { }
}

