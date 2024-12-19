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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ParserCombinatorsCronSpec extends AnyFlatSpec with CronSpec {
  def parser = parsing.parse
}

class AttoCronSpec extends AnyFlatSpec with CronSpec {
  def parser = atto.parser
}

class CronParserComparisonSpec extends AnyFlatSpec with Matchers {
  import CronSpec._

  "Parser-Combinators and Atto parsers" should "parse valid expressions with the same result" in {
    forAll(CronSpec.validExpressions) { expr =>
      parsing.parse(expr) shouldBe atto.parser(expr)
    }
  }
}
