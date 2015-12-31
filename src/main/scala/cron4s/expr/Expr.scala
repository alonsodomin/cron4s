package cron4s.expr

import cron4s.core.Bound
import simulacrum.typeclass

/**
  * Created by alonsodomin on 07/11/2015.
  */
abstract class Expr[V: Value]

case class Scalar[V: Value](value: V) extends Expr[V]
//case class Between[V: Value](min: Scalar[V], max: Scalar[V]) extends Expr[V]

object Between {
  //type BetweenQualities[V] = CanBeGrouped[V] with CanBeDivided[V]

  //implicit def qualities[V: Value](between: Between[V]): BetweenQualities[V] = new BetweenQualities[V]
  //sealed abstract class BetweenFieldExpr[T: Value] extends CanBeDivided[T] with CanBeGrouped[T]

  //implicit object IntBetween extends BetweenFieldExpr[Int]
  //implicit object StringBetween extends BetweenFieldExpr[String]
}

case class Several[V: Value](group: List[V])
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

