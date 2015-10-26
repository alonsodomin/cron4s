package cron4s.expr

import scala.collection.SortedSet

/**
 * Created by alonsodomin on 25/10/2015.
 */
package object constraint {
  import scala.language.implicitConversions
  import value._

  sealed trait Constraint[T, V <: ScalarVal[T, V]]

  trait ConstraintOps[T, V <: ScalarVal[T, V], C <: Constraint[T, V]] {
    def isValid(value: V): Boolean
  }

  case class Step(freq: IntVal) extends Constraint[Int, IntVal]
  object Step {
    implicit def toOps(every: Step): ConstraintOps[Int, IntVal, Step] = ???
  }

  case class Range[T, V <: ScalarVal[T, V]](min: V, max: V) extends Constraint[T, V]
  object Range {
    implicit def toOps[T, V <: ScalarVal[T, V]](range: Range[T, V]): ConstraintOps[T, V, Range[T, V]] = new ConstraintOps[T, V, Range[T, V]] {
      override def isValid(value: V): Boolean = value >= range.min && value <= range.max
    }
  }

  case class Group[T, V <: ScalarVal[T, V]](elements: SortedSet[V]) extends Constraint[T, V]
  object Group {
    implicit def toOps[T, V <: ScalarVal[T, V]](group: Group[T, V]): ConstraintOps[T, V, Group[T, V]] = new ConstraintOps[T, V, Group[T, V]] {
      override def isValid(value: V): Boolean = group.elements.contains(value)
    }
  }

}
