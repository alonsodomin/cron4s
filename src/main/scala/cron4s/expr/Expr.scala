package cron4s.expr

import simulacrum.typeclass

/**
  * Created by alonsodomin on 07/11/2015.
  */
abstract class Expr[V: Value] {
  def min[U <: CronUnit](unit: U)(implicit ev: CronUnitOps[V, U]): V
  def max[U <: CronUnit](unit: U)(implicit ev: CronUnitOps[V, U]): V

  def forward[U <: CronUnit](a: V, amount: Int, unit: U)(implicit ev: CronUnitOps[V, U]): Option[(V, Int)]
}

//@typeclass trait CanBeDivided[V] extends FieldExpr[V]
//@typeclass trait CanBeGrouped[V] extends FieldExpr[V]

sealed trait SpecialChar {
  def toChar: Char
}
object SpecialChar {
  case object Always extends SpecialChar {
    val toChar = '*'
  }
  case object Last extends SpecialChar {
    val toChar = 'L'
  }
}

case class Between[T: Value](min: T, max: T)
object Between {
  //sealed abstract class BetweenFieldExpr[T: Value] extends CanBeDivided[T] with CanBeGrouped[T]

  //implicit object IntBetween extends BetweenFieldExpr[Int]
  //implicit object StringBetween extends BetweenFieldExpr[String]
}

//case class Several[T : CanBeGrouped](group: List[T])
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

