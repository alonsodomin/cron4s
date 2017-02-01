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

package cron4s.testkit

import catalysts.Platform
import org.scalactic.anyvals.{PosInt, PosZInt}
import org.scalatest.{FunSuite, Matchers, PropSpec}
import org.scalatest.prop.{Configuration, GeneratorDrivenPropertyChecks}
import org.typelevel.discipline.scalatest.Discipline

/**
  * Created by alonsodomin on 31/01/2017.
  */
trait TestSettings extends Configuration {

  lazy val defaultPropertyCheckConfig: PropertyCheckConfiguration =
    PropertyCheckConfiguration(
      minSuccessful = if (Platform.isJvm) PosInt(50) else PosInt(5),
      minSize = PosZInt(0),
      sizeRange = if (Platform.isJvm) PosZInt(10) else PosZInt(5),
      workers = PosInt(1)
    )

  lazy val slowPropertyCheckConfig: PropertyCheckConfiguration =
    if (Platform.isJvm) defaultPropertyCheckConfig
    else PropertyCheckConfiguration(minSuccessful = 1, sizeRange = 1)

}


trait Cron4sLawSuite extends FunSuite
  with GeneratorDrivenPropertyChecks
  with Discipline
  with Matchers
  with TestSettings {

  override implicit val generatorDrivenConfig: PropertyCheckConfiguration =
    defaultPropertyCheckConfig

}

trait SlowCron4sLawSuite extends Cron4sLawSuite {

  override implicit val generatorDrivenConfig: PropertyCheckConfiguration =
    slowPropertyCheckConfig

}

abstract class Cron4sPropSpec extends PropSpec
  with Matchers
  with TestSettings {

  override implicit val generatorDrivenConfig: PropertyCheckConfiguration =
    defaultPropertyCheckConfig

}

abstract class SlowCron4sPropSpec extends PropSpec
  with Matchers
  with TestSettings {

  override implicit val generatorDrivenConfig: PropertyCheckConfiguration =
    slowPropertyCheckConfig

}
