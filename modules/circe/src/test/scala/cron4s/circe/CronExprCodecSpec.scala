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

package cron4s.circe

import cron4s.expr.CronExpr
import cron4s.testkit._
import cron4s.testkit.gen.CronGenerators

import io.circe.testing.{CodecTests, ArbitraryInstances => CirceArbitraryInstances}

class CronExprCodecSpec
    extends SlowCron4sLawSuite with CronGenerators with CirceArbitraryInstances {

  checkAll("Codec[CronExpr]", CodecTests[CronExpr].codec)

}
