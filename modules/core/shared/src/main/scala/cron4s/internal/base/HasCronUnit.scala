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
package base

import shapeless._

private[cron4s] trait HasCronUnit[T, F <: CronField] {
  def unit(t: T): CronUnit[F]
}

private[cron4s] object HasCronUnit extends HasCronUnitDerivation {
  def apply[T, F <: CronField](implicit ev: HasCronUnit[T, F]): HasCronUnit[T, F] = ev

  def instance[T, F <: CronField](f: T => CronUnit[F]): HasCronUnit[T, F] =
    new HasCronUnit[T, F] {
      def unit(t: T): CronUnit[F] = f(t)
    }
}

private[base] trait HasCronUnitDerivation extends HasCronUnitDerivation1 {

  implicit def deriveHasCronUnit[T, F <: CronField, C <: Coproduct](
      implicit
      G: Generic.Aux[T, C],
      unitC: Lazy[HasCronUnit[C, F]]
  ): HasCronUnit[T, F] =
    HasCronUnit.instance(a => unitC.value.unit(G.to(a)))

}

private[base] trait HasCronUnitDerivation1 extends HasCronUnitDerivation0 {

  implicit def deriveHasCronUnitCoproduct[H, T <: Coproduct, F <: CronField](
      implicit
      unitH: HasCronUnit[H, F],
      unitT: HasCronUnit[T, F]
  ): HasCronUnit[H :+: T, F] =
    HasCronUnit.instance { x =>
      x.head match {
        case Some(h) => unitH.unit(h)
        case None    => x.tail.map(unitT.unit).get
      }
    }

}

private[base] trait HasCronUnitDerivation0 {

  implicit def deriveHasCronUnit[F <: CronField]: HasCronUnit[CNil, F] =
    HasCronUnit.instance(_.impossible)

}
