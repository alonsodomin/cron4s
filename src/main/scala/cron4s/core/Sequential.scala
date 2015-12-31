package cron4s.core

import simulacrum.{op, typeclass}

/**
  * Created by alonsodomin on 31/12/2015.
  */
@typeclass trait Sequential[T] extends Bound[T] {

  def next(a: T): Option[T] = forward(a, 1).map(_._1)
  def previous(a: T): Option[T] = rewind(a, 1).map(_._1)

  def forward(a: T, amount: Int): Option[(T, Int)]
  def rewind(a: T, amount: Int): Option[(T, Int)] =
    forward(a, -amount)

}
