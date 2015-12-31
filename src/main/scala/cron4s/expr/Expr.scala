package cron4s.expr

import cron4s.core.{Sequential, Bound}
import simulacrum.typeclass

import scala.annotation.implicitNotFound

/**
  * Created by alonsodomin on 07/11/2015.
  */
trait Expr[V, U <: CronUnit] {
  //def valueType = implicitly[Value[V]]
  def asString: String
}

@implicitNotFound("Expression ${E} can not be grouped")
sealed trait CanBeGrouped[V, U <: CronUnit, +E[_, _] <: Expr[V, U]]

case class Scalar[V: Value, U <: CronUnit](value: V)(implicit ev: CronUnitOps[V, U]) extends Expr[V, U] with Ordered[Scalar[V, U]] {

  require(ev.indexOf(value).nonEmpty, s"Value $value is out of bounds for unit: ${ev.unit}")

  def compare(that: Scalar[V, U]): Int = {
    if (ev.lt(value, that.value)) -1
    else if (ev.gt(value, that.value)) 1
    else 0
  }

  def asString: String = value.toString

}
object Scalar {

  final class ScalarInstance[V: Value, U <: CronUnit](scalar: Scalar[V, U])(implicit unitOps: CronUnitOps[V, U])
      extends Bound[V] with Ordering[Scalar[V, U]] with CanBeGrouped[V, U, Scalar] {

    def max: V = scalar.value

    def min: V = scalar.value

    def compare(x: Scalar[V, U], y: Scalar[V, U]): Int = {
      if (unitOps.lt(x.value, y.value)) -1
      else if (unitOps.gt(x.value, y.value)) 1
      else 0
    }

  }

  implicit def scalarInstance[V: Value, U <: CronUnit](scalar: Scalar[V, U])
      (implicit ev: CronUnitOps[V, U]): ScalarInstance[V, U] = new ScalarInstance[V, U](scalar)

}

case class Between[V: Value, U <: CronUnit](min: Scalar[V, U], max: Scalar[V, U]) extends Expr[V, U] {

  require(min < max, s"$min should be less than $max")

  def asString: String = s"${min.asString}-${max.asString}"

}
object Between {

  final class BetweenInstance[V: Value, U <: CronUnit](between: Between[V, U]) extends Bound[V] {
    def min: V = between.min.value

    def max: V = between.max.value
  }

  implicit def betweenInstance[V: Value, U <: CronUnit](between: Between[V, U]): BetweenInstance[V, U] =
    new BetweenInstance[V, U](between)

  //type BetweenQualities[V] = CanBeGrouped[V] with CanBeDivided[V]

  //implicit def qualities[V: Value](between: Between[V]): BetweenQualities[V] = new BetweenQualities[V]
  //sealed abstract class BetweenFieldExpr[T: Value] extends CanBeDivided[T] with CanBeGrouped[T]

  //implicit object IntBetween extends BetweenFieldExpr[Int]
  //implicit object StringBetween extends BetweenFieldExpr[String]
}

case class Several[V: Value, U <: CronUnit, +E[_, _] <: Expr[V, U]](group: List[E])(implicit unitOps: CronUnitOps[V, U], ev3: E => CanBeGrouped[V, U, E]) extends Expr[V, U] {
  def asString: String = "group.map(_.asString).mkString(\",\")"
}
object Several {
  //sealed abstract class SeveralFieldExpr[T : CanBeGrouped] extends CanBeDivided[T]
  //implicit object IntSeveral extends SeveralFieldExpr[Int]
  //implicit object StringSeveral extends SeveralFieldExpr[String]
}

//case class Every[T : CanBeDivided](base: T, step: Int)
object Every {
  //sealed abstract class EveryFieldExpr[T : CanBeDivided] extends FieldExpr[T]
  //implicit object IntEvery extends EveryFieldExpr[Int]
  //implicit object StringEvery extends EveryFieldExpr[String]
}

