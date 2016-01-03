package cron4s.expr

import cron4s.core.{Indexed, Bound, Sequential}

/**
  * Created by alonsodomin on 07/11/2015.
  */
sealed trait Part[V, F <: CronField] extends Bound[V] with Sequential[V] {

  def matcher: Matcher[V]

}

sealed trait EnumerablePart[V, F <: CronField] extends Part[V, F]
sealed trait DivisiblePart[V, F <: CronField] extends Part[V, F]

sealed trait SpecialChar

case class Always[V: Value, F <: CronField](implicit unit: CronUnit[V, F]) extends DivisiblePart[V, F] with SpecialChar {

  def min: V = unit.min

  def max: V = unit.max

  def matcher: Matcher[V] = Matcher { x => true }

  def step(from: V, step: Int): Option[(V, Int)] = unit.step(from, step)

}
case object Last extends SpecialChar

case class Scalar[V: Value, F <: CronField](field: F, value: V)(implicit unit: CronUnit[V, F])
    extends EnumerablePart[V, F] with Ordered[Scalar[V, F]] {

  require(unit.indexOf(value).nonEmpty, s"Value $value is out of bounds for field: ${unit.field}")

  def min: V = value

  def max: V = value

  def compare(that: Scalar[V, F]): Int = {
    if (unit.lt(value, that.value)) -1
    else if (unit.gt(value, that.value)) 1
    else 0
  }

  def matcher: Matcher[V] = Matcher {
    x => unit.same(x, value)
  }

  def step(from: V, step: Int): Option[(V, Int)] =
    if (matcher(from)) Some((from, step)) else None

  def ~=[Y: Value](y: Scalar[Y, F])(implicit ev: CronUnit[Y, F]): Boolean =
    unit.same(value, y.value)

}
case class Between[V: Value, F <: CronField](begin: Scalar[V, F], end: Scalar[V, F])(implicit unit: CronUnit[V, F])
    extends EnumerablePart[V, F] with DivisiblePart[V, F] {

  require(begin < end, s"$begin should be less than $end")

  def min = begin.value

  def max = end.value

  def step(from: V, step: Int): Option[(V, Int)] = {
    if (matcher(from)) unit.focus(min, max).step(from, step)
    else None
  }

  def matcher: Matcher[V] = Matcher { x =>
    unit.gteq(x, begin.value) && unit.lteq(x, end.value)
  }

}
case class Several[V: Value, F <: CronField](values: IndexedSeq[EnumerablePart[V, F]])(implicit unit: CronUnit[V, F])
    extends DivisiblePart[V, F] with Bound[V] with Indexed[V] {

  def apply(index: Int): Option[V] = {
    if (index < 0 || index >= values.size) None
    else Some(values(index).min)
  }

  def min: V = values.head.min

  def max: V = values.reverse.head.max

  def indexOf(item: V): Option[Int] =
    Some(values.indexWhere(_.matcher(item))).filter(_ >= 0)

  def step(from: V, step: Int): Option[(V, Int)] = {
    if (matcher(from)) {
      indexOf(from).map(values(_)).flatMap(_.step(from, step))
    } else None
  }

  def matcher: Matcher[V] = Matcher { x =>
    values.exists(_.matcher(x))
  }

}
case class Every[V: Value, F <: CronField](value: DivisiblePart[V, F], freq: Int)(implicit unit: CronUnit[V, F])
    extends Part[V, F] with Sequential[V] {

  def min: V = value.min

  def max: V = value.max

  def matcher: Matcher[V] = Matcher { x =>
    if (value.matcher(x)) true
    else {
      var v = min
      var matched = false
      while (!unit.same(v, max) && !matched) {
        matched = unit.same(x, v)
        v = step(v, freq).get._1
      }
      matched
    }
  }

  override def next(from: V): Option[V] = value.step(from, freq).map(_._1)
  override def previous(from: V): Option[V] = value.step(from, -freq).map(_._1)

  def step(from: V, step: Int): Option[(V, Int)] = value.step(from, step)

}
