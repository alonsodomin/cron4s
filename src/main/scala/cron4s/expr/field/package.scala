package cron4s.expr

import scala.collection.SortedSet
import scala.reflect.ClassTag

/**
 * Created by alonsodomin on 25/10/2015.
 */
package object field {
  import scala.language.implicitConversions

  import unit._
  import value._

  sealed abstract class Field[V <: Val, U <: CronUnit](value: V, unit: U) {
    //def valueType(implicit tag: ClassTag[V]): ClassTag[V] = tag
  }

  case class MinuteField[V <: Val](value: V) extends Field[V, Minute.type](value, Minute)
  case class HourField[V <: Val](value: V) extends Field[V, Hour.type](value, Hour)

  MinuteField(IntVal(2))
  MinuteField(SymbolVal(Always))
  MinuteField(RangeVal(IntVal(2), IntVal(5)))
  MinuteField(FractionVal(RangeVal(IntVal(2), IntVal(5)), IntVal(2)))

}
