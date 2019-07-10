package cron4s
package base

import cats.data.NonEmptyVector

trait Productive[T, E] {
  def unfold(t: T): NonEmptyVector[E]
}

object Productive {
  def apply[T, E](implicit ev: Productive[T, E]): Productive[T, E] = ev

  def instance[T, E](f: T => NonEmptyVector[E]): Productive[T, E] = new Productive[T, E] {
    def unfold(t: T): NonEmptyVector[E] = f(t)
  }
}
