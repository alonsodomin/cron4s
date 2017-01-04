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

  def zeroStepSize(a: A, from: Int): IsEqual[Option[(Int, Int)]] = {
    val expected = if (from < a.min) {
      Some(a.min -> 0)
    } else if (from > a.max) {
      Some(a.max -> 0)
    } else {
      val lastIdx = a.range.lastIndexWhere(from >= _)
      Some(a.range(lastIdx) -> 0)
    }

    a.step(from, 0) <-> expected
  }

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

  def stepable(a: A, from: Int, stepSize: Int): IsEqual[Option[(Int, Int)]] = {
    if (a.range.isEmpty) {
      a.step(from, stepSize) <-> None
    } else if (from < a.min && stepSize >= 0) {
      a.step(from, stepSize) <-> Some(a.min -> stepSize)
    } else if (from > a.max && stepSize <= 0) {
      a.step(from, stepSize) <-> Some(a.max -> stepSize)
    } else {
      val index = a.range.lastIndexWhere(from >= _)
      val cursor = index + stepSize
      val newIdx = {
        val mod = cursor % a.range.size
        if (mod < 0) a.range.size + mod
        else mod
      }
      val newValue = a.range(newIdx)
      a.step(from, stepSize) <-> Some(newValue -> cursor / a.range.size)
    }
  }

}

object EnumeratedLaws {
  def apply[A](implicit ev: Enumerated[A]) = new EnumeratedLaws[A] { val TC = ev }
}
