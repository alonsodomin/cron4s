package cron4s.testkit.laws

import cron4s.CronField
import cron4s.types.HasCronField
import cron4s.syntax.field._

import scalaz.Scalaz._

/**
  * Created by alonsodomin on 27/08/2016.
  */
trait HasCronFieldLaws[A[_ <: CronField], F <: CronField] {
  implicit def TC: HasCronField[A, F]

  def min(a: A[F]): Boolean =
    a.min === a.range.min

  def max(a: A[F]): Boolean =
    a.max === a.range.max

  def forward(a: A[F], from: Int): Boolean =
    a.next(from) === a.step(from, TC.steppingUnit(a)).map(_._1)

  def backwards(a: A[F], from: Int): Boolean =
    a.prev(from) === a.step(from, -TC.steppingUnit(a)).map(_._1)

  def stepable(a: A[F], from: Int, stepSize: Int): Boolean = {
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

object HasCronFieldLaws {
  def apply[A[_ <: CronField], F <: CronField](implicit ev: HasCronField[A, F]) =
    new HasCronFieldLaws[A, F] { val TC = ev }
}
