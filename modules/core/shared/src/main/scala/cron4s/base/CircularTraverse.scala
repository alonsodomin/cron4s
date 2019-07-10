package cron4s
package base

import cats.Order
import cats.data.NonEmptyVector
import cats.implicits._

final case class Step private[cron4s] (amount: Int, direction: Direction) {
  require(amount >= 0, "Step amount must be a positive integer")

  def reverse: Step = copy(direction = direction.reverse)

}

object Step {
  def apply(stepSize: Int): Step =
    new Step(Math.abs(stepSize), Direction.ofSign(stepSize))
}

sealed abstract class Direction(private[cron4s] val sign: Int) {
  def reverse: Direction
}
object Direction {

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

trait CircularTraverse[F[_]] {
  protected[cron4s] def step[A: Order](fa: F[A], from: A, step: Step): (A, Int)

  final def step[A: Order](fa: F[A])(from: A, stepSize: Int): (A, Int) =
    step(fa, from, Step(stepSize))

  def next[A: Order](fa: F[A])(a: A): A = step(fa)(a, 1)._1
  def prev[A: Order](fa: F[A])(a: A): A = step(fa)(a, -1)._1

  def lowerBound[A: Order](fa: F[A]): A
  def upperBound[A: Order](fa: F[A]): A
  def narrowBounds[A: Order](fa: F[A])(lower: A, upper: A): F[A]
}

object CircularTraverse {

  def apply[F[_]](implicit ev: CircularTraverse[F]): CircularTraverse[F] = ev

  implicit val vectorCircularTraverse = new CircularTraverse[NonEmptyVector] {

    def step[A: Order](vector: NonEmptyVector[A], from: A, step: Step): (A, Int) = {
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
  }

  def narrowBounds[A: Order](fa: NonEmptyVector[A])(lower: A, upper: A): NonEmptyVector[A] = {
    if (lower === upper) NonEmptyVector.of(lower)
    else NonEmptyVector.fromVectorUnsafe {
      fa.toVector.sorted.dropWhile(_ < lower).takeWhile(_ <= upper)
    }
  }

}
