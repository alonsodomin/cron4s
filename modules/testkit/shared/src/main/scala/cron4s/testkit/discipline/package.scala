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

import cron4s.base.Direction
import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by alonsodomin on 03/01/2017.
  */
package object discipline {
  private[cron4s] val directionGen: Gen[Direction] =
    Gen.oneOf(Direction.Forward, Direction.Backwards)
  private[cron4s] implicit lazy val arbitraryDirection: Arbitrary[Direction] =
    Arbitrary(directionGen)
}
