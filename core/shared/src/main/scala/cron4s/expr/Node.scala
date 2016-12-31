package cron4s.expr

import cron4s.{CronField, CronUnit, generic}
import cron4s.types._
import cron4s.syntax._

import shapeless._

import scalaz.NonEmptyList
import scalaz.std.anyVal._
import scalaz.std.list._
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

  def range: IndexedSeq[Int]

}

object Node extends NodeInstances

final case class EachNode[+F <: CronField](implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = unit.range
  
  override def toString = "*"

}

final case class ConstNode[F <: CronField]
    (value: Int, textValue: Option[String] = None)
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = Vector(value)

  override def toString: String = textValue.getOrElse(value.toString)

}

final case class BetweenNode[F <: CronField]
    (begin: ConstNode[F], end: ConstNode[F])
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = {
    if (begin.value < end.value) begin.value to end.value
    else Vector.empty
  }

  override def toString = s"$begin-$end"

}

final case class SeveralNode[F <: CronField] private[expr]
    (values: NonEmptyList[SeveralMemberNode[F]])
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] =
    values.list.toVector.view.flatMap(_.range).distinct.sorted.toIndexedSeq

  override def toString: String =
    values.map(_.fold(generic.ops.show)).list.toList.mkString(",")

}

final case class EveryNode[F <: CronField]
    (value: FrequencyBaseNode[F], freq: Int)
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = {
    val elements = Stream.iterate[Option[(Int, Int)]](Some(value.min -> 0)) {
      _.flatMap { case (v, _) => value.step(v, freq) }
    }.flatten.takeWhile(_._2 < 1).map(_._1)

    elements.toVector
  }

  override def toString =
    s"${value.fold(generic.ops.show)}/$freq"

}

private[expr] trait NodeInstances extends LowPriorityNodeInstances {

  implicit def eachNodeInstance[F <: CronField]: Expr[EachNode, F] =
    new Expr[EachNode, F] {
      def unit(node: EachNode[F]): CronUnit[F] = node.unit

      def matches(node: EachNode[F]): Predicate[Int] = Predicate { x =>
        x >= min(node) && x <= max(node)
      }

      override def shows(node: EachNode[F]): String = node.toString

      def range(node: EachNode[F]): IndexedSeq[Int] = node.range
    }

  implicit def constNodeInstance[F <: CronField]: Expr[ConstNode, F] =
    new Expr[ConstNode, F] {
      def unit(node: ConstNode[F]): CronUnit[F] = node.unit
      def matches(node: ConstNode[F]): Predicate[Int] = equalTo(node.value)
      def range(node: ConstNode[F]): IndexedSeq[Int] = node.range
      override def shows(node: ConstNode[F]): String = node.toString
    }

  implicit def betweenNodeInstance[F <: CronField]: Expr[BetweenNode, F] =
    new Expr[BetweenNode, F] {

      def unit(node: BetweenNode[F]): CronUnit[F] = node.unit

      def matches(node: BetweenNode[F]): Predicate[Int] = Predicate { x =>
        if (node.begin.value < node.end.value)
          x >= node.begin.value && x <= node.end.value
        else false
      }

      def range(node: BetweenNode[F]): IndexedSeq[Int] = node.range

      override def shows(node: BetweenNode[F]): String = node.toString
    }

  implicit def severalNodeInstance[F <: CronField]
    (implicit elem: Lazy[Expr[SeveralMemberNode, F]]): Expr[SeveralNode, F] =
      new Expr[SeveralNode, F] {
        def unit(node: SeveralNode[F]): CronUnit[F] = node.unit

        def matches(node: SeveralNode[F]): Predicate[Int] =
          anyOf(node.values.map(elem.value.matches))

        def range(node: SeveralNode[F]): IndexedSeq[Int] = node.range

        override def shows(node: SeveralNode[F]): String = node.toString
      }

  implicit def everyNodeInstance[F <: CronField]
    (implicit base: Lazy[Expr[FrequencyBaseNode, F]]): Expr[EveryNode, F] =
    new Expr[EveryNode, F] {

      def unit(node: EveryNode[F]): CronUnit[F] = node.unit

      def matches(node: EveryNode[F]): Predicate[Int] =
        anyOf(range(node).map(equalTo(_)).toList)

      override def steppingUnit(a: EveryNode[F]): Int = a.freq

      def range(node: EveryNode[F]): IndexedSeq[Int] = node.range

      override def shows(node: EveryNode[F]): String = node.toString

    }

}

private[expr] trait LowPriorityNodeInstances {

  implicit def severalMemberNodeInstance[F <: CronField]: Expr[SeveralMemberNode, F] =
    new Expr[SeveralMemberNode, F] {
      def matches(node: SeveralMemberNode[F]): Predicate[Int] =
        node.fold(generic.ops.matches)

      def range(node: SeveralMemberNode[F]): IndexedSeq[Int] =
        node.fold(generic.ops.range)

      def unit(node: SeveralMemberNode[F]): CronUnit[F] =
        node.fold(generic.ops.unit)

      override def shows(node: SeveralMemberNode[F]): String =
        node.fold(generic.ops.show)
    }

  implicit def frequencyBaseNodeInstance[F <: CronField]: Expr[FrequencyBaseNode, F] =
    new Expr[FrequencyBaseNode, F] {
      def matches(node: FrequencyBaseNode[F]): Predicate[Int] =
        node.fold(generic.ops.matches)

      def range(node: FrequencyBaseNode[F]): IndexedSeq[Int] =
        node.fold(generic.ops.range)

      def unit(node: FrequencyBaseNode[F]): CronUnit[F] =
        node.fold(generic.ops.unit)

      override def shows(node: FrequencyBaseNode[F]): String =
        node.fold(generic.ops.show)
    }

  implicit def fieldNodeInstance[F <: CronField]: Expr[FieldNode, F] = new Expr[FieldNode, F] {
    def matches(node: FieldNode[F]): Predicate[Int] =
      node.fold(generic.ops.matches)

    def range(node: FieldNode[F]): IndexedSeq[Int] =
      node.fold(generic.ops.range)

    def unit(node: FieldNode[F]): CronUnit[F] =
      node.fold(generic.ops.unit)

    override def shows(node: FieldNode[F]): String =
      node.fold(generic.ops.show)
  }

}