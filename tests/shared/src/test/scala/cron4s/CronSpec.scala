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

import scala.util.{Failure, Success}
import org.scalatest.prop.TableFor3

/**
  * Created by alonsodomin on 12/04/2017.
  */
object CronSpec extends TableDrivenPropertyChecks {
  import CronField._

  final val AllEachExpr  = "* * * * * *"
  final val AnyDaysExpr  = "* * * ? * ?"
  final val TooLongExpr  = "* * * ? * * *"
  final val TooShortExpr = "* * * * *"

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

  val validExpressions = Table(
    "expression",
    "* 5 4 * * *",
    "* 0 0,12 1 */2 *",
    "* 5 4 * * sun",
    "* 0 0,12 1 */2 *",
    "0 0,5,10,15,20,25,30,35,40,45,50,55 * * * *",
    "0 1 2-4 * 4,5,6 */3",
    "1 5 4 * * mon-2,sun"
  )
}

trait CronSpec extends Matchers { this: AnyFlatSpec =>
  import CronSpec._

  def parser: cron4s.Parser
  def cron: CronImpl = new CronImpl(parser)

  "Cron" should "not parse an invalid expression" in {
    val _ =
      InvalidFieldCombination("Fields DayOfMonth and DayOfWeek can't both have the expression: *")

    forAll(InvalidExprs) { (desc: String, expr: String, err: Error) =>
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

    cron(exprStr) shouldBe Right(ValidExpr)

    cron.tryParse(exprStr) shouldBe Success(ValidExpr)

    cron.unsafeParse(exprStr) shouldBe ValidExpr
  }

}

class ParserCombinatorsCronSpec extends AnyFlatSpec with CronSpec {
  def parser = parsing.parse
}

class AttoCronSpec extends AnyFlatSpec with CronSpec {
  def parser = atto.Parser
}

class CronParserComparisonSpec extends AnyFlatSpec with Matchers {
  import CronSpec._

  "Parser-Combinators and Atto parsers" should "parse valid expressions with the same result" in {
    forAll(CronSpec.validExpressions) { expr =>
      parsing.parse(expr) shouldBe atto.Parser.parse(expr)
    }
  }
}
