package cron4s.expr

import cats._
import cats.implicits._
import cron4s.{CronField, CronUnit}
import cron4s.core.Sequential
import cron4s.matcher._

/**
  * Created by alonsodomin on 07/11/2015.
  */
sealed trait Expr[F <: CronField] extends Sequential[Int] {

  def matcher: Matcher[Int]

  def unit: CronUnit[F]

}

object Expr {

  sealed trait EnumerableExpr[F <: CronField] extends Expr[F]

  sealed trait DivisibleExpr[F <: CronField] extends Expr[F]

  sealed trait SpecialChar

  final case class AlwaysExpr[F <: CronField](implicit val unit: CronUnit[F])
    extends DivisibleExpr[F] with SpecialChar {

    def min: Int = unit.min

    def max: Int = unit.max

    def matcher: Matcher[Int] = Matcher.disjunction.monoid.empty

    def step(from: Int, step: Int): Option[(Int, Int)] =
      unit.step(from, step)

  }

  case object Last extends SpecialChar

  final case class ConstExpr[F <: CronField](field: F, value: Int, textValue: Option[String] = None)
                                            (implicit val unit: CronUnit[F])
    extends EnumerableExpr[F] {

    require(unit.indexOf(value).nonEmpty, s"Value $value is out of bounds for field: ${unit.field}")

    def min: Int = value

    def max: Int = value

    def matcher: Matcher[Int] = Matcher.equal(value)

    def step(from: Int, step: Int): Option[(Int, Int)] = {
      if (matcher.matches(from)) Some((from, step))
      else None
    }

  }

  final case class BetweenExpr[F <: CronField](begin: ConstExpr[F], end: ConstExpr[F])
                                              (implicit val unit: CronUnit[F])
    extends EnumerableExpr[F] with DivisibleExpr[F] {

    require(begin.value < end.value, s"$begin should be less than $end")

    def min: Int = begin.value

    def max: Int = end.value

    def matcher: Matcher[Int] = Matcher { x =>
      unit.gteq(x, begin.value) && unit.lteq(x, end.value)
    }

    def step(from: Int, step: Int): Option[(Int, Int)] = {
      if (matcher.matches(from))
        unit.focus(min, max).step(from, step)
      else
        None
    }

  }

  final case class SeveralExpr[F <: CronField](values: Vector[EnumerableExpr[F]])
                                              (implicit val unit: CronUnit[F])
    extends Expr[F] with DivisibleExpr[F] {

    def min: Int = values.head.min

    def max: Int = values.last.max

    def matcher: Matcher[Int] = Matcher.exists(values.map(_.matcher))

    def step(from: Int, step: Int): Option[(Int, Int)] = {
      if (matcher.matches(from)) {
        val range = min to max
        Option(range.indexOf(from)).filter(_ >= 0).map(values).
          flatMap {
            _.step(from, step)
          }
      } else None
    }

  }

  final case class EveryExpr[F <: CronField](value: DivisibleExpr[F], freq: Int)
                                            (implicit val unit: CronUnit[F])
    extends Expr[F] {

    def min: Int = value.min

    def max: Int = value.max

    def matcher: Matcher[Int] = Matcher { x =>
      if (value.matcher.matches(x)) true
      else {
        /*var v = min
      var matched = false
      while ((v != max) && !matched) {
        matched = x == v
        v = step(v, freq).get._1
      }
      matched*/
        false
      }
    }

    override def next(from: Int): Option[Int] = value.step(from, freq).map(_._1)

    override def previous(from: Int): Option[Int] = value.step(from, -freq).map(_._1)

    def step(from: Int, step: Int): Option[(Int, Int)] = value.step(from, step)

  }

}