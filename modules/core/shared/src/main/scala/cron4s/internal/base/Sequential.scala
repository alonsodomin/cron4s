package cron4s.internal
package base

import cats.Order
import cats.data.NonEmptyVector
import cats.implicits._

private[cron4s] final case class Step(amount: Int, direction: Direction) {
  require(amount >= 0, "Step amount must be a positive integer")

  def reverse: Step = copy(direction = direction.reverse)

}

private[cron4s] object Step {
  def apply(stepSize: Int): Step =
    new Step(Math.abs(stepSize), Direction.ofSign(stepSize))
}

private[cron4s] sealed abstract class Direction(private[cron4s] val sign: Int) {
  def reverse: Direction
}
private[cron4s] object Direction {

  def ofSign(step: Int): Direction =
    if (step >= 0) Forward
    else Backwards

  case object Forward extends Direction(1) {
    def reverse: Direction = Backwards
  }
  case object Backwards extends Direction(-1) {
    def reverse: Direction = Forward
  }
}

private[cron4s] trait Sequential[A, X] {
  protected[cron4s] def step(a: A, from: X, step: Step): (X, Int)

  final def step(a: A)(from: X, stepSize: Int): (X, Int) =
    step(a, from, Step(stepSize))

  def next(a: A)(from: X): X = step(a)(from, 1)._1
  def prev(a: A)(from: X): X = step(a)(from, -1)._1

  def narrowBounds(a: A)(lower: X, upper: X): A
}

private[cron4s] object Sequential {

  def apply[A, X](implicit ev: Sequential[A, X]): Sequential[A, X] = ev

  def by[A, B, X: Order](f: A => B)(implicit B: Sequential[B, X]): Sequential[A, X] =
    new Sequential[A, X] {
      def step(a: A, from: X, step: Step): (X, Int) = {
        B.step(f(a), from, step)
      }
    }

  implicit def vectorSequential[A: Order] = new Sequential[NonEmptyVector[A], A] {

    def step(vector: NonEmptyVector[A], from: A, step: Step): (A, Int) = {
      def nearestNeighbourIndex = step.direction match {
        case Direction.Forward =>
          val idx = vector.toVector.indexWhere(from < _)
          if (idx == -1) vector.size
          else idx

        case Direction.Backwards =>
          vector.toVector.lastIndexWhere(from > _)
      }

      def currentIdx =
        if (vector.toVector.contains(from)) {
          vector.toVector.indexOf(from)
        } else {
          val correction =
            if (step.amount != 0) step.direction.reverse.sign else 0
          nearestNeighbourIndex + correction
        }

      val pointer = currentIdx + (step.amount * step.direction.sign)
      val index = {
        val mod = pointer % vector.size
        if (mod < 0) vector.size + mod
        else mod
      }
      val offset = if (pointer < 0) {
        pointer - (vector.size - 1)
      } else {
        pointer
      }

      val newValue  = vector.getUnsafe(index.toInt)
      val carryOver = offset / vector.size
      (newValue, carryOver.toInt)
    }

    def narrowBounds(fa: NonEmptyVector[A])(lower: A, upper: A): NonEmptyVector[A] =
      if (lower === upper) NonEmptyVector.of(lower)
      else
        NonEmptyVector.fromVectorUnsafe {
          fa.toVector.sorted.dropWhile(_ < lower).takeWhile(_ <= upper)
        }
  }

  implicit def deriveSequentialFromProductive[A, X: Order](
    implicit productive: Productive[A, X]
  ): Sequential[A, X] = by(productive.unfold)

}
