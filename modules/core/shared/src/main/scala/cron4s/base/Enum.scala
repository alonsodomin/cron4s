package cron4s
package base

import scala.collection.SortedSet

trait Enum[A] {
  def members: SortedSet[A]
}

object Enum {
  def apply[A](implicit ev: Enum[A]): Enum[A] = ev

  def fromSet[A](elems: SortedSet[A]): Enum[A] = new Enum[A] {
    val members = elems
  }
}
