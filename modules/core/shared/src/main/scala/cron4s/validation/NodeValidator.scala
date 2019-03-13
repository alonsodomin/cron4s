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

package cron4s.validation

import cats.data._
import cats.implicits._

import cron4s.{CronField, CronUnit, InvalidField}
import cron4s.expr._
import cron4s.base.Enumerated
import cron4s.syntax.field._

/**
  * Created by alonsodomin on 18/12/2016.
  */
sealed trait NodeValidator[A] {

  def validate(node: A): List[InvalidField]

}

object NodeValidator extends NodeValidatorInstances {

  @inline def apply[A](implicit validator: NodeValidator[A]): NodeValidator[A] =
    validator

  def alwaysValid[A]: NodeValidator[A] = new NodeValidator[A] {
    def validate(node: A): List[InvalidField] = List.empty
  }

}

private[validation] trait NodeValidatorInstances extends LowPriorityNodeValidatorInstances {

  implicit def eachValidator[F <: CronField]: NodeValidator[EachNode[F]] =
    NodeValidator.alwaysValid[EachNode[F]]

  implicit def anyValidator[F <: CronField]: NodeValidator[AnyNode[F]] =
    NodeValidator.alwaysValid[AnyNode[F]]

  implicit def constValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
  ): NodeValidator[ConstNode[F]] = new NodeValidator[ConstNode[F]] {

    def validate(node: ConstNode[F]): List[InvalidField] =
      if (node.value < node.unit.min || node.value > node.unit.max) {
        List(
          InvalidField(
            node.unit.field,
            s"Value ${node.value} is out of bounds for field: ${node.unit.field}"
          )
        )
      } else List.empty

  }

  implicit def betweenValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
  ): NodeValidator[BetweenNode[F]] = new NodeValidator[BetweenNode[F]] {
    val subValidator = NodeValidator[ConstNode[F]]

    def validate(node: BetweenNode[F]): List[InvalidField] = {
      val baseErrors = List(
        subValidator.validate(node.begin),
        subValidator.validate(node.end)
      ).flatten

      if (node.begin.value >= node.end.value) {
        val error = InvalidField(
          node.unit.field,
          s"${node.begin.value} should be less than ${node.end.value}"
        )
        error :: baseErrors
      } else {
        baseErrors
      }
    }
  }

  implicit def severalValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
  ): NodeValidator[SeveralNode[F]] = new NodeValidator[SeveralNode[F]] {
    val elemValidator = NodeValidator[EnumerableNode[F]]

    def implicationErrorMsg(that: EnumerableNode[F], impliedBy: EnumerableNode[F]): String =
      s"Value '${that.show}' is implied by '${impliedBy.show}'"

    def checkImplication(
        curr: EnumerableNode[F]
    ): State[List[EnumerableNode[F]], List[List[InvalidField]]] = {
      lazy val currField = curr.unit.field

      def impliedByError(elem: EnumerableNode[F]): List[InvalidField] =
        if (curr.impliedBy(elem))
          List(InvalidField(currField, implicationErrorMsg(curr, elem)))
        else Nil

      def impliesError(elem: EnumerableNode[F]): List[InvalidField] =
        if (curr.implies(elem))
          List(InvalidField(currField, implicationErrorMsg(elem, curr)))
        else Nil

      State { seen =>
        val errors = seen.foldMap { elem =>
          impliesError(elem) ++ impliedByError(elem)
        }
        (curr :: seen) -> List(errors)
      }
    }

    def validate(node: SeveralNode[F]): List[InvalidField] = {
      val validation = node.values.foldMapM { elem =>
        val elemErrors = elemValidator.validate(elem)
        // If subexpressions in the elements are not valid, then
        // do not check for element implication
        if (elemErrors.isEmpty) checkImplication(elem)
        else
          State.pure[List[EnumerableNode[F]], List[List[InvalidField]]](List(elemErrors))
      }
      validation.map(_.flatten).runEmptyA.value
    }
  }

  implicit def everyValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
  ): NodeValidator[EveryNode[F]] = new NodeValidator[EveryNode[F]] {
    def validate(node: EveryNode[F]): List[InvalidField] = {
      lazy val baseErrors = NodeValidator[DivisibleNode[F]].validate(node.base)
      val evenlyDivided   = (node.base.range.size % node.freq) == 0
      if (!evenlyDivided) {
        InvalidField(
          node.unit.field,
          s"Step '${node.freq}' does not evenly divide the value '${node.base.show}'"
        ) :: baseErrors
      } else baseErrors
    }
  }

}

private[validation] trait LowPriorityNodeValidatorInstances {

  implicit def enumerableNodeValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
  ): NodeValidator[EnumerableNode[F]] = new NodeValidator[EnumerableNode[F]] {
    def validate(node: EnumerableNode[F]): List[InvalidField] =
      node.raw.fold(ops.validate)
  }

  implicit def divisibleNodeValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
  ): NodeValidator[DivisibleNode[F]] = new NodeValidator[DivisibleNode[F]] {
    def validate(node: DivisibleNode[F]): List[InvalidField] =
      node.raw.fold(ops.validate)
  }

  implicit def fieldNodeValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
  ): NodeValidator[FieldNode[F]] = new NodeValidator[FieldNode[F]] {
    def validate(node: FieldNode[F]): List[InvalidField] =
      node.raw.fold(ops.validate)
  }

  implicit def fieldNodeWithAnyValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
  ): NodeValidator[FieldNodeWithAny[F]] =
    new NodeValidator[FieldNodeWithAny[F]] {
      def validate(node: FieldNodeWithAny[F]): List[InvalidField] =
        node.raw.fold(ops.validate)
    }

}
