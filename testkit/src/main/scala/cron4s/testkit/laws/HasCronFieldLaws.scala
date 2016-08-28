package cron4s.testkit.laws

import cron4s.CronField
import cron4s.types.HasCronField
import cron4s.types.syntax.field._

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 27/08/2016.
  */
trait HasCronFieldLaws[A[_ <: CronField], F <: CronField] {
  implicit def TC: HasCronField[A, F]

  def min(a: A[F]): Boolean =
    a.min === a.members.min

  def max(a: A[F]): Boolean =
    a.max === a.members.max

  def forward(a: A[F], from: Int): Boolean =
    a.next(from) === a.step(from, 1).map(_._1)

  def backwards(a: A[F], from: Int): Boolean =
    a.prev(from) === a.step(from, -1).map(_._1)

}

object HasCronFieldLaws {
  def apply[A[_ <: CronField], F <: CronField](implicit ev: HasCronField[A, F]) =
    new HasCronFieldLaws[A, F] { def TC = ev }
}
