package cron4s.expr

import cron4s.{CronField, CronUnit, generic}
import cron4s.types._
import cron4s.syntax._

import shapeless._

import scalaz.NonEmptyList
import scalaz.std.anyVal._
import scalaz.std.vector._

/**
  * Generic representation of the expression node for a given field
  *
  * @author Antonio Alonso Dominguez
  */
sealed trait Node[+F <: CronField] {

  /**
    * Unit of this expression
    */
  val unit: CronUnit[F]

}

object Node extends NodeInstances

final case class EachNode[+F <: CronField](implicit val unit: CronUnit[F])
  extends Node[F] {

  override def toString = "*"

}

final case class ConstNode[F <: CronField]
    (value: Int, textValue: Option[String] = None)
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  override def toString = textValue.getOrElse(value.toString)

}

final case class BetweenNode[F <: CronField]
    (begin: ConstNode[F], end: ConstNode[F])
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  override def toString = s"$begin-$end"

}

final case class SeveralNode[F <: CronField] private[expr]
    (values: NonEmptyList[SeveralMemberNode[F]])
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  override def toString: String =
    values.map(_.fold(generic.ops.show)).list.toList.mkString(",")

}

final case class EveryNode[F <: CronField]
    (value: FrequencyBaseNode[F], freq: Int)
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  override def toString =
    s"${value.fold(generic.ops.show)}/$freq"

}

private[expr] trait NodeInstances extends LowPriorityNodeInstances {

  implicit def eachNodeInstance[F <: CronField]: Expr[EachNode, F] =
    new Expr[EachNode, F] {
      def unit(expr: EachNode[F]): CronUnit[F] = expr.unit

      def matches(e: EachNode[F]): Predicate[Int] = Predicate { x =>
        x >= min(e) && x <= max(e)
      }

      override def shows(expr: EachNode[F]): String = expr.toString

      def range(expr: EachNode[F]): Vector[Int] = expr.unit.range
    }

  implicit def constNodeInstance[F <: CronField]: Expr[ConstNode, F] =
    new Expr[ConstNode, F] {
      def unit(expr: ConstNode[F]): CronUnit[F] = expr.unit
      def matches(e: ConstNode[F]): Predicate[Int] = equalTo(e.value)
      def range(expr: ConstNode[F]): Vector[Int] = Vector(expr.value)
      override def shows(expr: ConstNode[F]): String = expr.toString
    }

  implicit def betweenNodeInstance[F <: CronField]: Expr[BetweenNode, F] =
    new Expr[BetweenNode, F] {

      def unit(expr: BetweenNode[F]): CronUnit[F] = expr.unit

      def matches(e: BetweenNode[F]): Predicate[Int] = Predicate { x =>
        if (e.begin.value < e.end.value)
          x >= e.begin.value && x <= e.end.value
        else false
      }

      def range(expr: BetweenNode[F]): Vector[Int] = {
        if (expr.begin.value < expr.end.value)
          (expr.begin.value to expr.end.value).toVector
        else Vector.empty
      }

      override def shows(expr: BetweenNode[F]): String = expr.toString
    }

  implicit def severalNodeInstance[F <: CronField]
    (implicit elem: Lazy[Expr[SeveralMemberNode, F]]): Expr[SeveralNode, F] =
      new Expr[SeveralNode, F] {
        def unit(expr: SeveralNode[F]): CronUnit[F] = expr.unit

        def matches(e: SeveralNode[F]): Predicate[Int] =
          anyOf(e.values.map(elem.value.matches))

        def range(expr: SeveralNode[F]): Vector[Int] =
          expr.values.list.toVector.flatMap(elem.value.range).distinct.sorted

        override def shows(expr: SeveralNode[F]): String = expr.toString
      }

  implicit def everyNodeInstance[F <: CronField]
    (implicit base: Lazy[Expr[FrequencyBaseNode, F]]): Expr[EveryNode, F] =
    new Expr[EveryNode, F] {

      def unit(expr: EveryNode[F]): CronUnit[F] = expr.unit

      def matches(e: EveryNode[F]): Predicate[Int] =
        anyOf(range(e).map(x => equalTo(x)))

      override def steppingUnit(a: EveryNode[F]): Int = a.freq

      def range(expr: EveryNode[F]): Vector[Int] = {
        val elements = Stream.iterate[Option[(Int, Int)]](Some(base.value.min(expr.value) -> 0)) {
          prev => prev.flatMap { case (v, _) => base.value.step(expr.value)(v, expr.freq) }
        }.flatten.takeWhile(_._2 < 1).map(_._1)

        elements.toVector
      }

      override def shows(expr: EveryNode[F]): String = expr.toString

    }

}

private[expr] trait LowPriorityNodeInstances {

  implicit def severalMemberNodeInstance[F <: CronField]: Expr[SeveralMemberNode, F] =
    new Expr[SeveralMemberNode, F] {
      def matches(e: SeveralMemberNode[F]): Predicate[Int] =
        e.fold(generic.ops.matches)

      def range(e: SeveralMemberNode[F]): Vector[Int] =
        e.fold(generic.ops.range)

      def unit(expr: SeveralMemberNode[F]): CronUnit[F] =
        expr.fold(generic.ops.unit)

      override def shows(expr: SeveralMemberNode[F]): String =
        expr.fold(generic.ops.show)
    }

  implicit def frequencyBaseNodeInstance[F <: CronField]: Expr[FrequencyBaseNode, F] =
    new Expr[FrequencyBaseNode, F] {
      def matches(e: FrequencyBaseNode[F]): Predicate[Int] =
        e.fold(generic.ops.matches)

      def range(e: FrequencyBaseNode[F]): Vector[Int] =
        e.fold(generic.ops.range)

      def unit(expr: FrequencyBaseNode[F]): CronUnit[F] =
        expr.fold(generic.ops.unit)

      override def shows(expr: FrequencyBaseNode[F]): String =
        expr.fold(generic.ops.show)
    }

  implicit def fieldNodeInstance[F <: CronField]: Expr[FieldNode, F] = new Expr[FieldNode, F] {
    def matches(expr: FieldNode[F]): Predicate[Int] =
      expr.fold(generic.ops.matches)

    def range(expr: FieldNode[F]): Vector[Int] =
      expr.fold(generic.ops.range)

    def unit(expr: FieldNode[F]): CronUnit[F] =
      expr.fold(generic.ops.unit)

    override def shows(expr: FieldNode[F]): String =
      expr.fold(generic.ops.show)
  }

}