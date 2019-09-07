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
package internal.base

import shapeless._

sealed trait SupportsCronField[A, F <: CronField] {
  def field(a: A): F
}
object SupportsCronField {
  def apply[A, F <: CronField](implicit ev: SupportsCronField[A, F]): SupportsCronField[A, F] = ev

  def instance[A, F <: CronField](f: A => F): SupportsCronField[A, F] =
    new SupportsCronField[A, F] {
      def field(a: A): F = f(a)
    }

  def refl[A, F <: CronField](implicit unit: CronUnit[F]): SupportsCronField[A, F] =
    instance(_ => unit.field)

  implicit def supportsCronFieldFromHasCronUnit[A, F <: CronField](
      implicit HCU: HasCronUnit[A, F]
  ): SupportsCronField[A, F] = new SupportsCronField[A, F] {
    def field(a: A): F = HCU.unit(a).field
  }

}

trait SupportsCronFieldDerivation extends SupportsCronFieldDerivation1 {
  implicit def deriveSupportsCronField[A, C <: Coproduct, F <: CronField](
      implicit
      G: Generic.Aux[A, C],
      supportsC: SupportsCronField[C, F]
  ): SupportsCronField[A, F] = SupportsCronField.instance(a => supportsC.field(G.to(a)))
}

trait SupportsCronFieldDerivation1 extends SupportsCronFieldDerivation0 {
  implicit def supportsCronFieldCNil[F <: CronField]: SupportsCronField[CNil, F] =
    SupportsCronField.instance(_.impossible)

  implicit def supportsCronFieldFromHeadHList[H, F <: CronField](
      implicit
      supportsHead: SupportsCronField[H, F]
  ): SupportsCronField[H :: HNil, F] =
    SupportsCronField.instance(ht => supportsHead.field(ht.head))
}

trait SupportsCronFieldDerivation0 {
  implicit def supportsCronFieldCoproduct[H, T <: Coproduct, F <: CronField](
      implicit
      supportsH: SupportsCronField[H, F],
      supportsT: SupportsCronField[T, F]
  ): SupportsCronField[H :+: T, F] = new SupportsCronField[H :+: T, F] {
    def field(ht: H :+: T): F =
      ht.head match {
        case Some(h) => supportsH.field(h)
        case None    => ht.tail.map(supportsT.field).get
      }
  }

  implicit def supportsCronFieldFromTailHList[H, T <: HList, F <: CronField](
      implicit
      supportsTail: SupportsCronField[T, F]
  ): SupportsCronField[H :: T, F] =
    SupportsCronField.instance(ht => supportsTail.field(ht.tail))
}
