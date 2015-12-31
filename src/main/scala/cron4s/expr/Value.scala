package cron4s.expr

import scala.annotation.implicitNotFound

/**
  * Created by alonsodomin on 30/12/2015.
  */
@implicitNotFound("${V} is not a valid value type")
sealed trait Value[V]
object Value {
  implicit object NumericValue extends Value[Int]
  implicit object TextValue extends Value[String]
}
