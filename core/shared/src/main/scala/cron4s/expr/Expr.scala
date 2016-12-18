package cron4s.expr

import cron4s.{CronField, CronUnit}
import cron4s.types._
import cron4s.syntax._

import shapeless._

import scala.language.{higherKinds, implicitConversions}

import scalaz.NonEmptyList
import scalaz.std.anyVal._
import scalaz.std.vector._

/**
  * Generic representation of the expression for a given field
  *
  * @author Antonio Alonso Dominguez
  */
sealed trait Expr[+F <: CronField] {

  /**
    * Unit of this expression
    */
  val unit: CronUnit[F]

}

object Expr extends ExprInstances

sealed trait SpecialChar

final case class EachExpr[+F <: CronField](implicit val unit: CronUnit[F])
  extends Expr[F] {

  override def toString = "*"

}

case object Last extends SpecialChar

final case class ConstExpr[F <: CronField]
    (value: Int, textValue: Option[String] = None)
    (implicit val unit: CronUnit[F])
  extends Expr[F] {

  //require(value >= unit.min && value <= unit.max, s"Value $value is out of bounds for field: ${unit.field}")

  /*override def compare(that: EnumerableExpr[F]): Int = {
    if (value > ops.min(that)) 1
    else if (value < ops.max(that)) -1
    else 0
  }*/

  override def toString = textValue.getOrElse(value.toString)

}

final case class BetweenExpr[F <: CronField]
    (begin: ConstExpr[F], end: ConstExpr[F])
    (implicit val unit: CronUnit[F])
  extends Expr[F] {

  //require(begin.value < end.value, s"$begin should be less than $end")

  /*override def compare(that: EnumerableExpr[F]): Int = {
    if (ops.min(this) > ops.max(that)) 1
    else if (ops.max(this) < ops.min(that)) -1
    else 0
  }*/

  override def toString = s"$begin-$end"

}

final case class SeveralExpr[F <: CronField] private[expr]
    (values: NonEmptyList[EnumExprAST[F]])
    (implicit val unit: CronUnit[F])
  extends Expr[F] {

  override def toString: String =
    values.map(_.fold(cron4s.util.show)).list.toList.mkString(",")

}

final case class EveryExpr[F <: CronField]
    (value: DivExprAST[F], freq: Int)
    (implicit val unit: CronUnit[F])
  extends Expr[F] {

  override def toString =
    s"${value.fold(cron4s.util.show)}/$freq"

}

private[expr] trait ExprInstances extends LowPriorityExprInstances {

  implicit def eachExprInstance[F <: CronField]: IsFieldExpr[EachExpr, F] =
    new IsFieldExpr[EachExpr, F] {
      def unit(expr: EachExpr[F]): CronUnit[F] = expr.unit

      def matches(e: EachExpr[F]): Predicate[Int] = Predicate { x =>
        x >= min(e) && x <= max(e)
      }

      def range(expr: EachExpr[F]): Vector[Int] = expr.unit.range
    }

  implicit def constExprInstance[F <: CronField]: IsFieldExpr[ConstExpr, F] =
    new IsFieldExpr[ConstExpr, F] {
      def unit(expr: ConstExpr[F]): CronUnit[F] = expr.unit
      def matches(e: ConstExpr[F]): Predicate[Int] = equalTo(e.value)
      def range(expr: ConstExpr[F]): Vector[Int] = Vector(expr.value)
    }

  implicit def betweenExprInstance[F <: CronField]: IsFieldExpr[BetweenExpr, F] =
    new IsFieldExpr[BetweenExpr, F] {

      def unit(expr: BetweenExpr[F]): CronUnit[F] = expr.unit

      def matches(e: BetweenExpr[F]): Predicate[Int] = Predicate { x =>
        x >= e.begin.value && x <= e.end.value
      }

      def range(expr: BetweenExpr[F]): Vector[Int] =
        (expr.begin.value to expr.end.value).toVector
    }

  implicit def severalExprInstance[F <: CronField]
    (implicit elem: Lazy[IsFieldExpr[EnumExprAST, F]]): IsFieldExpr[SeveralExpr, F] =
      new IsFieldExpr[SeveralExpr, F] {
        def unit(expr: SeveralExpr[F]): CronUnit[F] = expr.unit

        def matches(e: SeveralExpr[F]): Predicate[Int] =
          anyOf(e.values.map(elem.value.matches))

        def range(expr: SeveralExpr[F]): Vector[Int] =
          expr.values.list.toVector.flatMap(elem.value.range).distinct.sorted
      }

  implicit def everyExprInstance[F <: CronField]
    (implicit base: Lazy[IsFieldExpr[DivExprAST, F]]): IsFieldExpr[EveryExpr, F] =
    new IsFieldExpr[EveryExpr, F] {

      def unit(expr: EveryExpr[F]): CronUnit[F] = expr.unit

      def matches(e: EveryExpr[F]): Predicate[Int] =
        anyOf(range(e).map(x => equalTo(x)))

      override def steppingUnit(a: EveryExpr[F]): Int = a.freq

      def range(expr: EveryExpr[F]): Vector[Int] = {
        val elements = Stream.iterate[Option[(Int, Int)]](Some(base.value.min(expr.value) -> 0)) {
          prev => prev.flatMap { case (v, _) => base.value.step(expr.value)(v, expr.freq) }
        }.flatten.takeWhile(_._2 < 1).map(_._1)

        elements.toVector
      }

    }

}

private[expr] trait LowPriorityExprInstances {

  implicit def enumExpr[F <: CronField]: IsFieldExpr[EnumExprAST, F] = new IsFieldExpr[EnumExprAST, F] {
    def matches(e: EnumExprAST[F]): Predicate[Int] =
      e.fold(cron4s.util.matches)

    def range(e: EnumExprAST[F]): Vector[Int] =
      e.fold(cron4s.util.range)

    def unit(expr: EnumExprAST[F]): CronUnit[F] =
      expr.fold(cron4s.util.unit)
  }

  implicit def divExpr[F <: CronField]: IsFieldExpr[DivExprAST, F] = new IsFieldExpr[DivExprAST, F] {
    def matches(e: DivExprAST[F]): Predicate[Int] =
      e.fold(cron4s.util.matches)

    def range(e: DivExprAST[F]): Vector[Int] =
      e.fold(cron4s.util.range)

    def unit(expr: DivExprAST[F]): CronUnit[F] =
      expr.fold(cron4s.util.unit)
  }

  implicit def fieldExpr[F <: CronField]: IsFieldExpr[FieldExprAST, F] = new IsFieldExpr[FieldExprAST, F] {
    def matches(expr: FieldExprAST[F]): Predicate[Int] =
      expr.fold(cron4s.util.matches)

    def range(expr: FieldExprAST[F]): Vector[Int] =
      expr.fold(cron4s.util.range)

    def unit(expr: FieldExprAST[F]): CronUnit[F] =
      expr.fold(cron4s.util.unit)
  }

}
