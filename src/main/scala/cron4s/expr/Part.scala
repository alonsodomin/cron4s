package cron4s.expr

import cats.{Apply, Functor}
import cats.functor.Contravariant
import cron4s.core.{Indexed, Bound, Sequential}
import cats.syntax.contravariant._
import cron4s.matcher._

/**
  * Created by alonsodomin on 07/11/2015.
  */
sealed trait Part[F <: CronField] extends Bound[Int] with Sequential[Int] {

  def matcher: Matcher[Int]

  def matcherFor[X](implicit conv: (F, X) => Int): Matcher[X] =
    matcher.contramap(conv.curried(unit.field))

  def unit: CronUnit[F]

}

sealed trait EnumerablePart[F <: CronField] extends Part[F]
sealed trait DivisiblePart[F <: CronField] extends Part[F]

sealed trait SpecialChar

case class Always[F <: CronField](implicit val unit: CronUnit[F])
    extends DivisiblePart[F] with SpecialChar { self =>

  def min: Int = unit.min

  def max: Int = unit.max

  def matcher: Matcher[Int] = Matcher { v => true }

  def step(from: Int, step: Int): Option[(Int, Int)] = unit.step(from, step)

}

case object Last extends SpecialChar

case class Scalar[F <: CronField](field: F, value: Int, textValue: Option[String] = None)(implicit val unit: CronUnit[F])
    extends EnumerablePart[F] with Ordered[Scalar[F]] {

  require(unit.indexOf(value).nonEmpty, s"Value $value is out of bounds for field: ${unit.field}")

  def min: Int = value

  def max: Int = value

  def compare(that: Scalar[F]): Int = {
    if (unit.lt(value, that.value)) -1
    else if (unit.gt(value, that.value)) 1
    else 0
  }

  def matcher: Matcher[Int] = unit.matcherOn(value)

  def step(from: Int, step: Int): Option[(Int, Int)] =
    if (matcher.matches(from)) Some((from, step)) else None

}
case class Between[F <: CronField](begin: Scalar[F], end: Scalar[F])(implicit val unit: CronUnit[F])
    extends EnumerablePart[F] with DivisiblePart[F] {

  require(begin < end, s"$begin should be less than $end")

  def min = begin.value

  def max = end.value

  def step(from: Int, step: Int): Option[(Int, Int)] = {
    if (matcher.matches(from)) unit.focus(min, max).step(from, step)
    else None
  }

  def matcher: Matcher[Int] = Matcher { x =>
    unit.gteq(x, begin.value) && unit.lteq(x, end.value)
  }

}
case class Several[F <: CronField](values: IndexedSeq[EnumerablePart[F]])(implicit val unit: CronUnit[F])
    extends DivisiblePart[F] with Indexed[Int] {

  def apply(index: Int): Option[Int] = {
    if (index < 0 || index >= values.size) None
    else Some(values(index).min)
  }

  def min: Int = values.head.min

  def max: Int = values.reverse.head.max

  def indexOf(item: Int): Option[Int] =
    Some(values.indexWhere(_.matcher.matches(item))).filter(_ >= 0)

  def step(from: Int, step: Int): Option[(Int, Int)] = {
    if (matcher.matches(from)) {
      indexOf(from).map(values(_)).flatMap(_.step(from, step))
    } else None
  }

  def matcher: Matcher[Int] = Matcher { x =>
    values.exists(_.matcher.matches(x))
  }

}
case class Every[F <: CronField](value: DivisiblePart[F], freq: Int)(implicit val unit: CronUnit[F])
    extends Part[F] {

  def min: Int = value.min

  def max: Int = value.max

  def matcher: Matcher[Int] = Matcher { x =>
    if (value.matcher.matches(x)) true
    else {
      var v = min
      var matched = false
      while ((v != max) && !matched) {
        matched = x == v
        v = step(v, freq).get._1
      }
      matched
    }
  }

  override def next(from: Int): Option[Int] = value.step(from, freq).map(_._1)
  override def previous(from: Int): Option[Int] = value.step(from, -freq).map(_._1)

  def step(from: Int, step: Int): Option[(Int, Int)] = value.step(from, step)

}
