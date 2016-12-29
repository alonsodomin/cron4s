package cron4s.testkit.laws

import cron4s.CronField
import cron4s.types.Enumerated
import cron4s.syntax.enumerated._

import scalaz.Scalaz._

/**
  * Created by alonsodomin on 27/08/2016.
  */
trait EnumeratedLaws[A] {
  implicit def TC: Enumerated[A]

  def min(a: A): Boolean =
    a.min === a.range.min

  def max(a: A): Boolean =
    a.max === a.range.max

  def forward(a: A, from: Int): Boolean =
    a.next(from) === a.step(from, TC.steppingUnit(a)).map(_._1)

  def backwards(a: A, from: Int): Boolean =
    a.prev(from) === a.step(from, -TC.steppingUnit(a)).map(_._1)

  def stepable(a: A, from: Int, stepSize: Int): Boolean = {
    if (a.range.isEmpty) {
      a.step(from, stepSize) === None
    } else if (from < a.min && stepSize >= 0) {
      a.step(from, stepSize) === Some(a.min -> (stepSize * TC.steppingUnit(a)))
    } else if (from > a.max && stepSize <= 0) {
      a.step(from, stepSize) === Some(a.max -> (stepSize * TC.steppingUnit(a)))
    } else {
      val index = a.range.lastIndexWhere(from >= _)
      val cursor = index + (stepSize * TC.steppingUnit(a))
      val newIdx = {
        val mod = cursor % a.range.size
        if (mod < 0) a.range.size + mod
        else mod
      }
      val newValue = a.range(newIdx)
      a.step(from, stepSize) === Some(newValue -> cursor / a.range.size)
    }
  }

}

object EnumeratedLaws {
  def apply[A](implicit ev: Enumerated[A]) = new EnumeratedLaws[A] { val TC = ev }
}
