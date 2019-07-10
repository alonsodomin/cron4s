package cron4s
package base

import cats.implicits._

trait CircularTraverse[F[_]] {
  protected[cron4s] def step[A](fa: F[A], from: A, step: Step): (A, Int)

  final def step[A](fa: F[A])(from: A, stepSize: Int): (A, Int) =
    step(t, from, Step(stepSize))

  def next[A](fa: F[A])(a: A): A = step(fa)(a, 1)._1
  def prev[A](fa: F[A])(a: A): A = step(fa)(a, -1)._1
}

object CircularTraverse {

  def apply[F[_]](implicit ev: CircularTraverse[F]): CircularTraverse[F] = ev

  implicit val vectorCircularTraverse = new CircularTraverse[Vector] {
    def step[A](vector: Vector[A], from: A, step: Step): (A, Int) = {
      def nearestNeighbourIndex = step.direction match {
        case Direction.Forward =>
          val idx = vector.indexWhere(from < _)
          if (idx == -1) vector.size
          else idx

        case Direction.Backwards =>
          vector.lastIndexWhere(from > _)
      }

      def currentIdx =
        if (vector.contains(from)) {
          vector.indexOf(from)
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

      val newValue  = vector(index)
      val carryOver = offset / vector.size
      (newValue, carryOver)
    }
  }

}