package cron4s.internal
package base

import scala.collection.SortedSet

private[cron4s] trait Enum[A] {
  def members: SortedSet[A]
}

private[cron4s] object Enum {
  def apply[A](implicit ev: Enum[A]): Enum[A] = ev

  def fromSet[A](elems: SortedSet[A]): Enum[A] = new Enum[A] {
    val members = elems
  }
}
