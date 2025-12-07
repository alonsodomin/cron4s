/*
 * Copyright 2017 Antonio Alonso Dominguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cron4s

import cats.data.NonEmptyList

import cron4s.expr._

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.util.Failure
import org.scalatest.prop.TableFor3
import CronField._

/**
  * Created by alonsodomin on 12/04/2017.
  */
object AWSCronSpec extends TableDrivenPropertyChecks {

  final val AllEachExpr        = "* * * * * *"
  final val AnyDaysExpr        = "* * ? * ? *"
  final val TooLongExpr        = "* * * ? * * *"
  final val TooShortExpr       = "* * * * *"
  final val OutOfYearRangeExpr = "* * ? * * 2200"

  final val InvalidExprs: TableFor3[String, String, Error] =
    Table(
      ("description", "expression", "expected error"),
      (
        "all stars",
        AllEachExpr,
        InvalidCron(
          NonEmptyList.of(
            InvalidFieldCombination(
              "Fields DayOfMonth and DayOfWeek can't both have the expression: *"
            )
          )
        )
      ),
      (
        "symbol ? at two positions",
        AnyDaysExpr,
        InvalidCron(
          NonEmptyList.of(
            InvalidFieldCombination(
              "Fields DayOfMonth and DayOfWeek can't both have the expression: ?"
            )
          )
        )
      ),
      // Strangely, `lastFailure` rendered by scala.util.parsing.combinator.phrase is not consistent depending on how to run test.
      // cron4sJVM/testOnly cron4s.CronSpec passes, while cron4sJVM/test fails with
      // `Left(cron4s.ParseFailed: * expected at position 14 but found '?')`.
      /*(
        "too long expression",
        TooLongExpr,
        ParseFailed("* expected", 7, Some("?"))
      ),*/
      (
        "too short expression",
        TooShortExpr,
        ExprTooShort
      ),
      (
        "out of year range",
        OutOfYearRangeExpr,
        InvalidCron(
          NonEmptyList.of(
            InvalidField(CronField.Year, "Value 2200 is out of bounds for field: Year")
          )
        )
      )
    )

  // "toString" returns quartz cron expression
  // final val ValidExpr = CronExpr(
  //   SeveralNode(BetweenNode[Second](ConstNode(17), ConstNode(30)), ConstNode[Second](5)),
  //   EachNode[Minute],
  //   ConstNode[Hour](12),
  //   EachNode[DayOfMonth],
  //   EachNode[Month],
  //   AnyNode[DayOfWeek]
  // )

  val validExpressions = Table(
    "expression",
    "0 10 * * ? *",         // Run at 10:00 am (UTC+0) every day
    "15 12 * * ? *",        // Run at 12:15 pm (UTC+0) every day
    "0 18 ? * MON-FRI *",   // Run at 6:00 pm (UTC+0) every Monday through Friday
    "0 8 1 * ? *",          // Run at 8:00 am (UTC+0) every 1st day of the month
    "0/15 * * * ? *",       // Run every 15 minutes
    "0/10 * ? * MON-FRI *", // Run every 10 minutes Monday through Friday
    "0/5 8-17 ? * MON-FRI *", // Run every 5 minutes Monday through Friday between 8:00 am and 5:55 pm (UTC+0)
    "0/30 20-2 ? * MON-FRI *" // Run every 30 minutes Monday through Friday between 10:00 pm on the starting day to 2:00 am on the following day (UTC)
  )
}

trait AWSCronSpec extends Matchers { this: AnyFlatSpec =>
  import AWSCronSpec._

  def parser: cron4s.parser.Parser
  def cron: CronImpl = new CronImpl(parser)

  "Cron" should "parse valid expression" in {

    forAll(validExpressions) { (expr) =>
      val parsed = cron(expr)
      println(parsed)
    }
  }

  "Cron" should "not parse an invalid expression" in {
    val _ =
      InvalidFieldCombination("Fields DayOfMonth and DayOfWeek can't both have the expression: *")

    forAll(InvalidExprs) { (desc: String, expr: String, err: Error) =>
      val parsed = cron(expr)
      parsed shouldBe Left(err)

      val parsedTry = cron.tryParse(expr)
      parsedTry should matchPattern { case Failure(`err`) => }

      intercept[Error] {
        cron.unsafeParse(expr)
      }
    }
  }

  it should "parse complexe day express" in {
    val value1 = cron("0 * ? * MON-WED,FRI-SUN *")
    value1 shouldBe Right(
      CronExpr(
        ConstNode[Second](0),
        ConstNode[Minute](0),
        EachNode[Hour],
        AnyNode[DayOfMonth],
        EachNode[Month],
        SeveralNode(
          BetweenNode[DayOfWeek](ConstNode(0), ConstNode(2)),
          BetweenNode[DayOfWeek](ConstNode(4), ConstNode(6))
        ),
        Some(EachNode[Year])
      )
    )
  }
}
