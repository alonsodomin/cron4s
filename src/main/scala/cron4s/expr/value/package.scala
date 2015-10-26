package cron4s.expr

/**
 * Created by alonsodomin on 25/10/2015.
 */
package object value {
  import scala.language.implicitConversions

  sealed trait Val
  sealed trait GroupableVal extends Val
  sealed trait FractionableVal extends Val

  sealed trait ScalarVal[T] extends Val with FractionableVal {
    def value: T
  }

  sealed trait SpecialChar
  case object Always extends SpecialChar {
    override def toString = "*"
  }
  case object Last extends SpecialChar {
    override def toString = "L"
  }

  case class IntVal(value: Int) extends ScalarVal[Int] with GroupableVal
  case class StringVal(value: String) extends ScalarVal[String] with GroupableVal
  case class SymbolVal(value: SpecialChar) extends ScalarVal[SpecialChar]

  case class RangeVal[T, V <: ScalarVal[T]](min: V, max: V) extends Val with GroupableVal with FractionableVal
  case class SetVal[V <: GroupableVal](set: Set[V]) extends Val
  case class FractionVal[T <: FractionableVal](bounds: T, step: IntVal) extends Val

}
