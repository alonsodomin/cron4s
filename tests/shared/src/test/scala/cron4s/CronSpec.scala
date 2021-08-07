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
import cron4s.syntax.all._

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.util.{Failure, Success}

/**
  * Created by alonsodomin on 12/04/2017.
  */
object CronSpec extends TableDrivenPropertyChecks {
  import CronField._

  final val AllEachExpr  = "* * * * * *"
  final val AnyDaysExpr  = "* * * ? * ?"
  final val TooLongExpr  = "* * * ? * * *"
  final val TooShortExpr = "* * * * *"

  final val InvalidExprs =
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
      (
        "too long expression",
        TooLongExpr,
        ParseFailed("'/' expected but   found", 12, None)
      ),
      (
        "too short expression",
        TooShortExpr,
        ExprTooShort
      )
    )

  final val ValidExpr = CronExpr(
    SeveralNode(BetweenNode[Second](ConstNode(17), ConstNode(30)), ConstNode[Second](5)),
    EachNode[Minute],
    ConstNode[Hour](12),
    EachNode[DayOfMonth],
    EachNode[Month],
    AnyNode[DayOfWeek]
  )

}

class CronSpec extends AnyFlatSpec with Matchers {
  import CronSpec._

  "Cron" should "not parse an invalid expression" in {
    forAll(InvalidExprs) { (_: String, expr: String, err: Error) =>
      val parsed = Cron(expr)
      parsed shouldBe Left(err)

      val parsedTry = Cron.tryParse(expr)
      parsedTry should matchPattern { case Failure(`err`) => }

      intercept[Error] {
        Cron.unsafeParse(expr)
      }
    }
  }

  it should "parse a valid expression" in {
    val exprStr = ValidExpr.toString

    Cron(exprStr) shouldBe Right(ValidExpr)

    Cron.tryParse(exprStr) shouldBe Success(ValidExpr)

    Cron.unsafeParse(exprStr) shouldBe ValidExpr
  }
}
