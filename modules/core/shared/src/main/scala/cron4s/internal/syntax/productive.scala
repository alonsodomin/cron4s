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
package internal
package syntax

import cats.data.NonEmptyVector

import cron4s.internal.base.Productive

private[syntax] class ProductiveOps[A, X](self: A, tc: Productive[A, X]) {
  def unfold: NonEmptyVector[X] = tc.unfold(self)
}

private[syntax] trait ProductiveSyntax {
  implicit def toProductiveOps[A, X](
      target: A
  )(implicit instance: Productive[A, X]): ProductiveOps[A, X] =
    new ProductiveOps[A, X](target, instance)
}

private[cron4s] object productive extends ProductiveSyntax
