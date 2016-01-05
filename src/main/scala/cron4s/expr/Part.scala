package cron4s.expr

import cats.{Apply, Functor}
import cats.functor.Contravariant
import cron4s.core.{Indexed, Bound, Sequential}
import cats.syntax.contravariant._
import cron4s.matcher._

/**
  * Created by alonsodomin on 07/11/2015.
  */
sealed trait Part[F <: CronField] extends Sequential[Int] {

  def matcher: Matcher[Int]

  def matcherFor[X](implicit conv: (F, X) => Int): Matcher[X] =
    matcher.contramap(conv.curried(unit.field))

  def unit: CronUnit[F]

}

sealed trait EnumerablePart[F <: CronField] extends Part[F]
sealed trait DivisiblePart[F <: CronField] extends Part[F]

sealed trait SpecialChar

final case class Always[F <: CronField](implicit val unit: CronUnit[F])
    extends DivisiblePart[F] with SpecialChar { self =>

  def min: Int = unit.min

  def max: Int = unit.max

  def matcher: Matcher[Int] = Matcher { v => true }

  def step(from: Int, step: Int): Option[(Int, Int)] =
    unit.step(from, step)

}

case object Last extends SpecialChar

final case class Scalar[F <: CronField](field: F, value: Int, textValue: Option[String] = None)
      (implicit val unit: CronUnit[F])
    extends EnumerablePart[F] {

  require(unit.indexOf(value).nonEmpty, s"Value $value is out of bounds for field: ${unit.field}")

  def min: Int = value

  def max: Int = value

  def matcher: Matcher[Int] = unit.matcherOn(value)

  def step(from: Int, step: Int): Option[(Int, Int)] = {
    if (matcher.matches(from)) Some((from, step))
    else None
  }

}

final case class Between[F <: CronField](begin: Scalar[F], end: Scalar[F])
      (implicit val unit: CronUnit[F])
    extends EnumerablePart[F] with DivisiblePart[F] {

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

final case class Several[F <: CronField](values: IndexedSeq[EnumerablePart[F]])
      (implicit val unit: CronUnit[F])
    extends Part[F] with DivisiblePart[F] {

  def min: Int = values.head.min

  def max: Int = values.last.max

  def matcher: Matcher[Int] = Matcher { x =>
    values.exists(_.matcher.matches(x))
  }

  def step(from: Int, step: Int): Option[(Int, Int)] = {
    if (matcher.matches(from)) {
      val range = min to max
      Option(range.indexOf(from)).filter(_ >= 0).map(values).
        flatMap { _.step(from, step) }
    } else None
  }

}

case class Every[F <: CronField](value: DivisiblePart[F], freq: Int)
      (implicit val unit: CronUnit[F])
    extends Part[F] {

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