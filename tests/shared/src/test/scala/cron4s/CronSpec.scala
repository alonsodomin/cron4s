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

import org.scalatest.{FlatSpec, Matchers}

import scala.util.Failure

/**
  * Created by alonsodomin on 12/04/2017.
  */
object CronSpec {

  final val AllEachExpr = "* * * * * *"
  final val AnyDaysExpr = "* * * ? * ?"

  final val InvalidExprs = List(AllEachExpr, AnyDaysExpr)

}

class CronSpec extends FlatSpec with Matchers {
  import CronSpec._

  "Cron" should "not parse an expression with all *" in {
    val expectedError = InvalidFieldCombination("Fields DayOfMonth and DayOfWeek can't both have the expression: *")

    val parsed = Cron(AllEachExpr)
    parsed shouldBe Left(InvalidCron(NonEmptyList.of(expectedError)))

    val parsedTry = Cron.tryParse(AllEachExpr)
    parsedTry should matchPattern { case Failure(InvalidCron(_)) => }

    intercept[InvalidCron] {
      Cron.unsafeParse(AllEachExpr)
    }
  }

  it should "not parse an expression with ? in both DayOfMonth and DayOfWeek" in {
    val expectedError = InvalidFieldCombination("Fields DayOfMonth and DayOfWeek can't both have the expression: ?")

    val parsed = Cron(AnyDaysExpr)
    parsed shouldBe Left(InvalidCron(NonEmptyList.of(expectedError)))

    val parsedTry = Cron.tryParse(AnyDaysExpr)
    parsedTry should matchPattern { case Failure(InvalidCron(_)) => }

    intercept[InvalidCron] {
      Cron.unsafeParse(AnyDaysExpr)
    }
  }

}
