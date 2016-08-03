package cron4s.expr

import cron4s.{CronField, CronUnit}
import cron4s.matcher._
import cron4s.types.Sequential

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 07/11/2015.
  */
sealed trait Expr[F <: CronField] extends Sequential[Int] {

  def matches: Matcher[Int]

  /**
    * An expression `A` is implied by an expression `B` iff for all
    * values x that `A.matches(x)`, then `B.matches(x)`
    *
    * @param expr
    * @tparam E
    * @return
    */
  def impliedBy[E <: Expr[F]](expr: E): Boolean =
    range.forall(expr.matches(_))

  def unit: CronUnit[F]

  def step(from: Int, step: Int): Option[(Int, Int)] = {
    if (from < unit.min || from > unit.max) None
    else Sequential.sequential(range).step(from, step)
  }

  val range: IndexedSeq[Int]

}

sealed trait EnumerableExpr[F <: CronField] extends Expr[F] with Ordered[EnumerableExpr[F]]
sealed trait DivisibleExpr[F <: CronField] extends Expr[F]

sealed trait SpecialChar

final case class AnyExpr[F <: CronField](implicit val unit: CronUnit[F])
  extends DivisibleExpr[F] with SpecialChar {

  def min: Int = unit.min

  def max: Int = unit.max

  def matches: Matcher[Int] = Matcher { x =>
    if (unit.range.contains(x)) true
    else false
  }

  val range = unit.range

}

case object Last extends SpecialChar

final case class ConstExpr[F <: CronField](field: F, value: Int, textValue: Option[String] = None)
                                          (implicit val unit: CronUnit[F])
  extends EnumerableExpr[F] {

  require(unit.indexOf(value).nonEmpty, s"Value $value is out of bounds for field: ${unit.field}")

  def min: Int = value

  def max: Int = value

  def matches: Matcher[Int] = equal(value)

  override def compare(that: EnumerableExpr[F]): Int = {
    if (value < that.min) 1
    else if (value > that.max) -1
    else 0
  }

  val range = Vector(value)

}

final case class BetweenExpr[F <: CronField](begin: ConstExpr[F], end: ConstExpr[F])
                                            (implicit val unit: CronUnit[F])
  extends EnumerableExpr[F] with DivisibleExpr[F] {

  require(begin.value < end.value, s"$begin should be less than $end")

  def min: Int = begin.value

  def max: Int = end.value

  def matches: Matcher[Int] = Matcher { x =>
    x >= begin.value && x <= end.value
  }

  override def compare(that: EnumerableExpr[F]): Int = {
    if (min > that.max) 1
    else if (max < that.min) -1
    else 0
  }

  val range = min to max

}

final case class SeveralExpr[F <: CronField] private[expr]
    (values: Vector[EnumerableExpr[F]])
    (implicit val unit: CronUnit[F])
  extends Expr[F] with DivisibleExpr[F] {

  require(values.nonEmpty, "Expression should contain at least one element")

  val min: Int = values.head.min

  val max: Int = values.last.max

  def matches: Matcher[Int] = anyOf(values.map(_.matches))

  val range: IndexedSeq[Int] = values.flatMap(_.range).distinct.sorted

}
object SeveralExpr {
  import validation.validateSeveralExpr

  def apply[F <: CronField](head: EnumerableExpr[F], tail: EnumerableExpr[F]*)(implicit unit: CronUnit[F]) = {
    validateSeveralExpr(NonEmptyList[EnumerableExpr[F]](head, tail: _*)) match {
      case Success(expr) => expr
      case Failure(errors) =>
        val msg = errors.list.toList.mkString("\n")
        throw new IllegalArgumentException(msg)
    }
  }
}

final case class EveryExpr[F <: CronField](value: DivisibleExpr[F], freq: Int)
                                          (implicit val unit: CronUnit[F])
  extends Expr[F] {

  def min: Int = value.min

  def max: Int = value.max

  def matches: Matcher[Int] = anyOf(range.toVector.map(x => equal(x)))

  override def next(from: Int): Option[Int] = super.step(from, freq).map(_._1)

  override def previous(from: Int): Option[Int] = super.step(from, -freq).map(_._1)

  override def step(from: Int, step: Int): Option[(Int, Int)] = super.step(from, step * freq)

  val range: IndexedSeq[Int] = {
    val elements = Stream.iterate[Option[(Int, Int)]](Some(min, 0)) {
      prev => prev.flatMap { case (v, _) => value.step(v, freq) }
    }.flatten.takeWhile(_._2 < 1).map(_._1)

    elements.toIndexedSeq
  }

}
