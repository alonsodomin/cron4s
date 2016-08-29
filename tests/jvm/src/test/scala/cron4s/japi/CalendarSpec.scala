package cron4s.japi

import java.time.{LocalDateTime, ZoneOffset}
import java.util.{Calendar, TimeZone}

import cron4s._
import cron4s.expr._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}
import shapeless._

/**
  * Created by alonsodomin on 07/08/2016.
  */
class CalendarSpec extends PropSpec with TableDrivenPropertyChecks with Matchers {
  import CronField._
  import calendar._

  val onlyTuesdaysAt12 = CronExpr(ConstExpr(Minute, 0) :: ConstExpr(Hour, 12) ::
    AnyExpr[DayOfMonth.type] :: AnyExpr[Month.type] :: ConstExpr(DayOfWeek, 1) :: HNil)
  val onlySundays = CronExpr(AnyExpr[Minute.type] :: AnyExpr[Hour.type] ::
    AnyExpr[DayOfMonth.type] :: AnyExpr[Month.type] :: ConstExpr(DayOfWeek, 6) :: HNil)

  def calendarDate(minute: Int, hour: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): Calendar = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val timeMillis = LocalDateTime.of(2016, month, dayOfMonth, hour, minute).
      toInstant(ZoneOffset.UTC).toEpochMilli
    cal.setTimeInMillis(timeMillis)

    cal
  }

  val samples = Table(
    ("expr", "from", "stepSize", "expected"),
    (onlyTuesdaysAt12, calendarDate(0, 0, 1, 8, 1), 1, calendarDate(0, 12, 2, 8, 2))
  )

  property("step") {
    forAll(samples) { (expr: CronExpr, initial: Calendar, stepSize: Int, expected: Calendar) =>
      expr.step(initial, stepSize) shouldBe Some(expected)
    }
  }

}
