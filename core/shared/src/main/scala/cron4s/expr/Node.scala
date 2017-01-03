package cron4s.expr

import cron4s.{CronField, CronUnit, generic}
import cron4s.types._
import cron4s.syntax._

import shapeless._

import scalaz.NonEmptyList
import scalaz.std.anyVal._
import scalaz.std.list._

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

}

final case class ConstNode[F <: CronField]
    (value: Int, textValue: Option[String] = None)
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = Vector(value)

}

final case class BetweenNode[F <: CronField]
    (begin: ConstNode[F], end: ConstNode[F])
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = {
    if (begin.value < end.value) begin.value to end.value
    else Vector.empty
  }

}

final case class SeveralNode[F <: CronField]
    (values: NonEmptyList[EnumerableNode[F]])
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] =
    values.list.toVector.view.flatMap(_.range).distinct.sorted.toIndexedSeq

}

object SeveralNode {
  def apply[F <: CronField](head: EnumerableNode[F], tail: EnumerableNode[F]*)
                           (implicit unit: CronUnit[F]): SeveralNode[F] =
    SeveralNode(NonEmptyList(head, tail: _*))
}

final case class EveryNode[F <: CronField]
    (value: DivisibleNode[F], freq: Int)
    (implicit val unit: CronUnit[F])
  extends Node[F] {

  lazy val range: IndexedSeq[Int] = {
    val elements = Stream.iterate[Option[(Int, Int)]](Some(value.min -> 0)) {
      _.flatMap { case (v, _) => value.step(v, freq) }
    }.flatten.takeWhile(_._2 < 1).map(_._1)

    elements.toVector
  }

}

private[expr] trait NodeInstances extends LowPriorityNodeInstances {

  implicit def eachNodeInstance[F <: CronField]: Expr[EachNode, F] =
    new Expr[EachNode, F] {
      def unit(node: EachNode[F]): CronUnit[F] = node.unit

      def matches(node: EachNode[F]): Predicate[Int] = Predicate { x =>
        x >= min(node) && x <= max(node)
      }

      override def shows(node: EachNode[F]): String = "*"

      def range(node: EachNode[F]): IndexedSeq[Int] = node.range
    }

  implicit def constNodeInstance[F <: CronField]: Expr[ConstNode, F] =
    new Expr[ConstNode, F] {
      def unit(node: ConstNode[F]): CronUnit[F] = node.unit
      def matches(node: ConstNode[F]): Predicate[Int] = equalTo(node.value)
      def range(node: ConstNode[F]): IndexedSeq[Int] = node.range
      override def shows(node: ConstNode[F]): String = node.textValue.getOrElse(node.value.toString)
    }

  implicit def betweenNodeInstance[F <: CronField]
    (implicit elemExpr: Lazy[Expr[ConstNode, F]]): Expr[BetweenNode, F] =
      new Expr[BetweenNode, F] {

        def unit(node: BetweenNode[F]): CronUnit[F] = node.unit

        def matches(node: BetweenNode[F]): Predicate[Int] = Predicate { x =>
          if (node.begin.value < node.end.value)
            x >= node.begin.value && x <= node.end.value
          else false
        }

        def range(node: BetweenNode[F]): IndexedSeq[Int] = node.range

        override def shows(node: BetweenNode[F]): String =
          s"${elemExpr.value.shows(node.begin)}-${elemExpr.value.shows(node.end)}"
      }

  implicit def severalNodeInstance[F <: CronField]
    (implicit elem: Lazy[Expr[EnumerableNode, F]]): Expr[SeveralNode, F] =
      new Expr[SeveralNode, F] {
        def unit(node: SeveralNode[F]): CronUnit[F] = node.unit

        def matches(node: SeveralNode[F]): Predicate[Int] =
          anyOf(node.values.map(elem.value.matches))

        def range(node: SeveralNode[F]): IndexedSeq[Int] = node.range

        override def shows(node: SeveralNode[F]): String =
          node.values.map(elem.value.shows).list.toList.mkString(",")
      }

  implicit def everyNodeInstance[F <: CronField]
    (implicit baseExpr: Lazy[Expr[DivisibleNode, F]]): Expr[EveryNode, F] =
    new Expr[EveryNode, F] {

      def unit(node: EveryNode[F]): CronUnit[F] = node.unit

      def matches(node: EveryNode[F]): Predicate[Int] =
        anyOf(range(node).map(equalTo(_)).toList)

      override protected[cron4s] def baseStepSize(node: EveryNode[F]): Int = node.freq

      def range(node: EveryNode[F]): IndexedSeq[Int] = node.range

      override def shows(node: EveryNode[F]): String =
        s"${baseExpr.value.shows(node.value)}/${node.freq}"

    }

}

private[expr] trait LowPriorityNodeInstances {

  implicit def enumerableNodeInstance[F <: CronField]: Expr[EnumerableNode, F] =
    new Expr[EnumerableNode, F] {
      def matches(node: EnumerableNode[F]): Predicate[Int] =
        node.fold(generic.ops.matches)

      def range(node: EnumerableNode[F]): IndexedSeq[Int] =
        node.fold(generic.ops.range)

      def unit(node: EnumerableNode[F]): CronUnit[F] =
        node.fold(generic.ops.unit)

      override def shows(node: EnumerableNode[F]): String =
        node.fold(generic.ops.show)
    }

  implicit def divisibleNodeInstance[F <: CronField]: Expr[DivisibleNode, F] =
    new Expr[DivisibleNode, F] {
      def matches(node: DivisibleNode[F]): Predicate[Int] =
        node.fold(generic.ops.matches)

      def range(node: DivisibleNode[F]): IndexedSeq[Int] =
        node.fold(generic.ops.range)

      def unit(node: DivisibleNode[F]): CronUnit[F] =
        node.fold(generic.ops.unit)

      override def shows(node: DivisibleNode[F]): String =
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