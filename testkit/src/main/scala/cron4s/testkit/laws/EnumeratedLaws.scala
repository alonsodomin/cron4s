package cron4s.testkit.laws

import cron4s.testkit._
import cron4s.types.Enumerated
import cron4s.syntax.enumerated._

/**
  * Created by alonsodomin on 27/08/2016.
  */
trait EnumeratedLaws[A] {
  implicit def TC: Enumerated[A]

  def min(a: A): IsEqual[Int] =
    a.min <-> a.range.min

  def max(a: A): IsEqual[Int] =
    a.max <-> a.range.max

  def forward(a: A, from: Int): IsEqual[Option[Int]] =
    a.next(from) <-> a.step(from, 1).map(_._1)

  def backwards(a: A, from: Int): IsEqual[Option[Int]] =
    a.prev(from) <-> a.step(from, -1).map(_._1)

  def zeroStepSize(a: A, from: Int): IsEqual[Option[(Int, Int)]] =
    a.step(from, 0) <-> Some(from -> 0)

  def fromMinToMinForwards(a: A): IsEqual[Option[(Int, Int)]] =
    a.step(a.min, a.range.size) <-> Some(a.min -> 1)

  def fromMaxToMaxForwards(a: A): IsEqual[Option[(Int, Int)]] =
    a.step(a.max, a.range.size) <-> Some(a.max -> 1)

  def fromMinToMaxForwards(a: A): IsEqual[Option[(Int, Int)]] =
    a.step(a.min, a.range.size - 1) <-> Some(a.max -> 0)

  def fromMinToMaxBackwards(a: A): IsEqual[Option[(Int, Int)]] =
    a.step(a.min, -1) <-> Some(a.max -> -1)

  def fromMaxToMinForwards(a: A): IsEqual[Option[(Int, Int)]] =
    a.step(a.max, 1) <-> Some(a.min -> 1)

  def fromMaxToMinBackwards(a: A): IsEqual[Option[(Int, Int)]] =
    a.step(a.max, -(a.range.size - 1)) <-> Some(a.min -> 0)

  def backAndForth(a: A, from: Int, stepSize: Int): IsEqual[Option[Int]] = {
    val moved = a.step(from, stepSize).map(_._1)
    val returned = moved.flatMap { from2 =>
      a.step(from2, stepSize * -1).map(_._1)
    }

    val expected = moved.map { _ =>
      if (stepSize == 0) from
      else {
        val idx = if (stepSize > 0) {
          val i = a.range.lastIndexWhere(from >= _)
          if (i == -1) a.range.size - 1
          else i
        } else {
          val i = a.range.indexWhere(from <= _)
          if (i == -1) 0
          else i
        }
        a.range(idx)
      }
    }

    returned <-> expected
  }

}

object EnumeratedLaws {
  def apply[A](implicit ev: Enumerated[A]) = new EnumeratedLaws[A] { val TC = ev }
}
