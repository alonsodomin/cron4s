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

import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Success}

/**
  * Created by alonsodomin on 12/04/2017.
  */
object CronSpec {
  import CronField._

  final val AllEachExpr = "* * * * * *"
  final val AnyDaysExpr = "* * * ? * ?"

  final val InvalidExprs = List(AllEachExpr, AnyDaysExpr)

  final val ValidExpr = CronExpr(
    SeveralNode(BetweenNode[Second](ConstNode(17), ConstNode(30)), ConstNode[Second](5)),
    EachNode[Minute],
    ConstNode[Hour](12),
    EachNode[DayOfMonth],
    EachNode[Month],
    AnyNode[DayOfWeek]
  )

}

class CronSpec extends FlatSpec with Matchers {
  import CronSpec._

  "Cron" should "not parse an expression with all *" in {
    val expectedError =
      InvalidFieldCombination("Fields DayOfMonth and DayOfWeek can't both have the expression: *")

    val parsed = Cron(AllEachExpr)
    parsed shouldBe Left(InvalidCron(NonEmptyList.of(expectedError)))

    val parsedTry = Cron.tryParse(AllEachExpr)
    parsedTry should matchPattern { case Failure(InvalidCron(_)) => }

    intercept[InvalidCron] {
      Cron.unsafeParse(AllEachExpr)
    }
  }

  it should "not parse an expression with ? in both DayOfMonth and DayOfWeek" in {
    val expectedError =
      InvalidFieldCombination("Fields DayOfMonth and DayOfWeek can't both have the expression: ?")

    val parsed = Cron(AnyDaysExpr)
    parsed shouldBe Left(InvalidCron(NonEmptyList.of(expectedError)))

    val parsedTry = Cron.tryParse(AnyDaysExpr)
    parsedTry should matchPattern { case Failure(InvalidCron(_)) => }

    intercept[InvalidCron] {
      Cron.unsafeParse(AnyDaysExpr)
    }
  }

  it should "parse a valid expression" in {
    val exprStr = ValidExpr.toString

    Cron(exprStr) shouldBe Right(ValidExpr)

    Cron.tryParse(exprStr) shouldBe Success(ValidExpr)

    Cron.unsafeParse(exprStr) shouldBe ValidExpr
  }

  it should "compile a valid expression" in {
    val secs = "17-30,5"
    val expr = cron"$secs * 12 * * ?"

    expr shouldBe ValidExpr
  }

}
