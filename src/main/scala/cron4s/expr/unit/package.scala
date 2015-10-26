package cron4s.expr

/**
 * Created by alonsodomin on 25/10/2015.
 */
package object unit {
  import constraint._
  import value._

  sealed trait CronUnit

  trait CronUnitOps[U <: CronUnit, V <: ScalarVal[_]] {
    lazy val min = range._1
    lazy val max = range._2

    final def >>(v: V, steps: Int = 1) = forward(v, steps)
    def forward(v: V, steps: Int = 1): V

    final def <<(v: V, steps: Int = 1) = rewind(v, steps)
    def rewind(v: V, steps: Int = 1): V

    def range: (V, V)
    def values: Seq[V]
  }

  private[expr] trait NumericCronUnitOps[U <: CronUnit] extends CronUnitOps[U, IntVal] {

    lazy val values: Seq[IntVal] = for { i <- min.value to max.value } yield IntVal(i)

    def forward(v: IntVal, steps: Int): IntVal = IntVal((v.value + steps) % values.size)
    def rewind(v: IntVal, steps: Int): IntVal = IntVal(Math.abs(v.value - steps))

  }

  private[expr] trait NamedCronUnitOps[U <: CronUnit] extends CronUnitOps[U, StringVal] {
    def names: Seq[String]

    lazy val range = (values.head, values.reverse.head)
    lazy val values = names.map(StringVal.apply)

    def forward(v: StringVal, steps: Int): StringVal = {
      val idx = names.indexOf(v.value)
      values((idx + steps) % values.size)
    }
    def rewind(v: StringVal, steps: Int): StringVal = {
      val idx = names.indexOf(v.value)
      values(Math.abs(idx - steps))
    }

  }

  case object Minute extends CronUnit
  implicit object MinuteOps extends NumericCronUnitOps[Minute.type] {
    val range = (IntVal(0), IntVal(59))
  }

  case object Hour extends CronUnit
  implicit object HourOps extends NumericCronUnitOps[Hour.type] {
    val range = (IntVal(0), IntVal(23))
  }

  case object DayOfMonth extends CronUnit
  implicit object DayOfMonthOps extends NumericCronUnitOps[DayOfMonth.type] {
    val range = (IntVal(1), IntVal(31))
  }

  case object Month extends CronUnit {
    val Names = Seq("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec")
  }
  implicit object NumericMonthOps extends NumericCronUnitOps[Month.type] {
    val range = (IntVal(1), IntVal(12))
  }
  implicit object NamedMonthOps extends NamedCronUnitOps[Month.type] {
    val names = Month.Names
  }

  case object DayOfWeek extends CronUnit {
    val Names = Seq("sun", "mon", "tue", "wed", "thu", "fri", "sat")
  }
  implicit object NumericDayOfWeekOps extends NumericCronUnitOps[DayOfWeek.type] {
    val range = (IntVal(0), IntVal(6))
  }
  implicit object NamedDayOfWeekOps extends NamedCronUnitOps[DayOfWeek.type] {
    val names = DayOfWeek.Names
  }

  case object Year extends CronUnit
  implicit  object YearOps extends NumericCronUnitOps[Year.type] {
    def range = (IntVal(0), IntVal(99))
  }

}
