package cron4s.testkit

import org.scalacheck.Prop

import scala.language.implicitConversions

import scalaz.Equal

/**
  * Created by alonsodomin on 03/01/2017.
  */
package object discipline {
  implicit def isEqualToProp[A: Equal](isEqual: IsEqual[A]): Prop =
    isEqual.lhs ?== isEqual.rhs
}
