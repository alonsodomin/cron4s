package cron4s.expr

/**
  * Created by alonsodomin on 30/12/2015.
  */
sealed trait Value[V]
object Value {
  implicit object NumericValue extends Value[Int]
  implicit object TextValue extends Value[String]
}
