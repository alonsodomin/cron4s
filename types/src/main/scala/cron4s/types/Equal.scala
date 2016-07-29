package cron4s.types

import scala.{specialized => sp}

/**
  * Created by alonsodomin on 29/07/2016.
  */
trait Equal[@sp A] extends Any with Serializable {

  def eqv(x: A, y: A): Boolean

}

object Equal {
  @inline final def apply[A](implicit ev: Equal[A]): Equal[A] = ev
}
