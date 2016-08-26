package cron4s.expr

import cron4s.{CronField, CronUnit}
import cron4s.types._

import scalaz._
import Scalaz._

import scala.language.implicitConversions
import scala.language.higherKinds

/**
  * Created by alonsodomin on 07/11/2015.
  */
sealed trait Expr[F <: CronField] {

  val unit: CronUnit[F]

  val range: IndexedSeq[Int]

}

sealed trait DivisibleE[F <: CronField] extends Expr[F]
sealed trait EnumerableE[F <: CronField] extends Expr[F]

object Expr extends ExprInstances

sealed trait SpecialChar

final case class AnyExpr[F <: CronField](implicit val unit: CronUnit[F])
  extends Expr[F] with DivisibleE[F] with SpecialChar {

  val range = unit.range

}

case object Last extends SpecialChar

final case class ConstExpr[F <: CronField]
    (field: F, value: Int, textValue: Option[String] = None)
    (implicit val unit: CronUnit[F])
  extends Expr[F] with DivisibleE[F] {

  //require(unit.indexOf(value).nonEmpty, s"Value $value is out of bounds for field: ${unit.field}")

  val range = Vector(value)

}

final case class BetweenExpr[F <: CronField]
    (begin: ConstExpr[F], end: ConstExpr[F])
    (implicit val unit: CronUnit[F])
  extends Expr[F] with DivisibleE[F] {

  require(begin.value < end.value, s"$begin should be less than $end")

  val range = begin.value to end.value

}

final case class SeveralExpr[E[_] <: EnumerableE[F], F <: CronField] private[expr]
    (values: Vector[E[F]])
    (implicit val unit: CronUnit[F], ev: SeqEnumerableExpr[E, F])
  extends Expr[F] {

  require(values.nonEmpty, "Expression should contain at least one element")

  val range: IndexedSeq[Int] = values.flatMap(ev.range).distinct.sorted

}
object SeveralExpr {
  import validation.validateSeveral

  def apply[E[_] <: Expr[F], F <: CronField]
      (head: E[F], tail: E[F]*)
      (implicit unit: CronUnit[F], ev: SeqEnumerableExpr[E, F]) = {
    validateSeveral(NonEmptyList[E[F]](head, tail: _*)) match {
      case Success(expr) => expr
      case Failure(errors) =>
        val msg = errors.list.toList.mkString("\n")
        throw new IllegalArgumentException(msg)
    }
  }

}

final case class EveryExpr[E[_] <: Expr[F], F <: CronField]
    (value: E[F], freq: Int)
    (implicit val unit: CronUnit[F], ev: SeqDivisibleExpr[E, F])
  extends Expr[F] {

  val range: Vector[Int] = {
    val elements = Stream.iterate[Option[(Int, Int)]](Some(value.min -> 0)) {
      prev => prev.flatMap { case (v, _) => value.step(v, freq) }
    }.flatten.takeWhile(_._2 < 1).map(_._1)

    elements.toVector
  }

}

private[expr] trait ExprInstances extends ExprInstances0 {

  implicit def anyExprLike[F <: CronField]: SequencedExpr[AnyExpr, F] =
    new SequencedExprBase[AnyExpr, F] with SeqDivisibleExpr[AnyExpr, F] {
      override def matches(e: AnyExpr[F]): Predicate[Int] = Predicate { x =>
        x >= min(e) && x <= max(e)
      }
    }

  trait ConstExprLike[F] extends SequencedExprBase[ConstExpr, F] with SeqEnumerableExpr[ConstExpr, F]

  implicit def constExprLike[F <: CronField]: ConstExprLike[F] =
    new ConstExprLike[F] {
      override def matches(e: ConstExpr[F]): Predicate[Int] = equal(e.value)

      override def compare(x: ConstExpr[F], y: ConstExpr[F]): Int = {
        if (x.value < min(y)) 1
        else if (x.value > max(y)) -1
        else 0
      }
    }

  trait SequencedBetweenExpr[F] extends SequencedExprBase[BetweenExpr, F]
    with SeqEnumerableExpr[BetweenExpr, F]
    with SeqDivisibleExpr[BetweenExpr, F]

  implicit def betweenExprLike[F <: CronField]: SequencedBetweenExpr[F] =
    new SequencedBetweenExpr[F] {

      override def matches(e: BetweenExpr[F]): Predicate[Int] = Predicate { x =>
        x >= e.begin.value && x <= e.end.value
      }

      override def compare(x: BetweenExpr[F], y: BetweenExpr[F]): Int = {
        if (min(x) > max(y)) 1
        else if (max(x) < min(y)) -1
        else 0
      }
    }

  trait SequencedSeveralExpr[E[_] <: Expr[F], F <: CronField]
    extends SequencedExprBase[SeveralExpr[E, ?], F]
      with SeqDivisibleExpr[SeveralExpr[E, ?], F]

  implicit def severalExprLike[E[_] <: Expr[F], F <: CronField]
    (implicit ev: SeqEnumerableExpr[E, F]): SequencedSeveralExpr[E, F] =
      new SequencedSeveralExpr[E, F] {
        override def matches(e: SeveralExpr[E, F]): Predicate[Int] =
          anyOf(e.values.map(ev.matches))
      }

  implicit def everyExprLike[E[_] <: Expr[F], F <: CronField]: SequencedExpr[EveryExpr[E, ?], F] =
    new SequencedExprBase[EveryExpr[E, ?], F] {

      override def matches(e: EveryExpr[E, F]): Predicate[Int] =
        anyOf(e.range.map(x => equal(x)))

      override def next(e: EveryExpr[E, F])(from: Int): Option[Int] =
        super.step(e)(from, e.freq).map(_._1)

      override def prev(e: EveryExpr[E, F])(from: Int): Option[Int] =
        super.step(e)(from, -e.freq).map(_._1)

      override def step(e: EveryExpr[E, F])(from: Int, step: Int): Option[(Int, Int)] =
        super.step(e)(from, step * e.freq)

    }

}

private[expr] trait ExprInstances0 {
  private[expr] trait SequencedExprBase[E[_] <: Expr[F], F <: CronField] extends SequencedExpr[E, F] {
    override def matches(e: E[F]): Predicate[Int] =
      Predicate { x => e.range.contains(x) }

    override def range(expr: E[F]): IndexedSeq[Int] = expr.range
  }

  implicit def genericExprLike[F <: CronField]: SequencedExpr[Expr, F] =
    new SequencedExprBase[Expr, F] { }
}

