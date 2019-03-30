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

package cron4s.syntax

import cron4s.base.{Step, Enumerated}

private[syntax] class EnumeratedOps[T](self: T, tc: Enumerated[T]) {
  def max: Int = tc.max(self)
  def min: Int = tc.min(self)

  def range: IndexedSeq[Int] = tc.range(self)
}

private[syntax] trait EnumeratedSyntax {

  implicit def toEnumeratedOps[T](target: T)(implicit tc: Enumerated[T]): EnumeratedOps[T] =
    new EnumeratedOps[T](target, tc)

}

object enumerated extends EnumeratedSyntax
