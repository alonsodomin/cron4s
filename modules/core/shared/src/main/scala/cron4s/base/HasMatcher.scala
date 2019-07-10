package cron4s
package base

trait HasMatcher[A, T] {
  def matcher(a: A): Predicate[T]
}
object HasMatcher {
  def apply[A, T](implicit ev: HasMatcher[A, T]): HasMatcher[A, T] = ev
}