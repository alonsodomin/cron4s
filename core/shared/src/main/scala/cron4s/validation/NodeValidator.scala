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

import cron4s.{CronField, CronUnit, FieldError}
import cron4s.expr._
import cron4s.generic.ops
import cron4s.types.Enumerated
import cron4s.syntax.enumerated._
import cron4s.syntax.expr._

import scalaz._
import Scalaz._

/**
  * Created by alonsodomin on 18/12/2016.
  */
sealed trait NodeValidator[A] {

  def validate(node: A): List[FieldError]

}

object NodeValidator extends NodeValidatorInstances {

  @inline def apply[A](implicit validator: NodeValidator[A]): NodeValidator[A] = validator

}

private[validation] trait NodeValidatorInstances extends LowPriorityNodeValidatorInstances {

  implicit def eachValidator[F <: CronField]: NodeValidator[EachNode[F]] =
    new NodeValidator[EachNode[F]] {
      def validate(node: EachNode[F]): List[FieldError] = List.empty
    }

  implicit def constValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
    ): NodeValidator[ConstNode[F]] = new NodeValidator[ConstNode[F]] {

      def validate(node: ConstNode[F]): List[FieldError] = {
        if (node.value < node.unit.min || node.value > node.unit.max) {
          List(FieldError(
            node.unit.field,
            s"Value ${node.value} is out of bounds for field: ${node.unit.field}"
          ))
        } else List.empty
      }

    }

  implicit def betweenValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
    ): NodeValidator[BetweenNode[F]] = new NodeValidator[BetweenNode[F]] {
      def validate(node: BetweenNode[F]): List[FieldError] = {
        val subValidator = NodeValidator[ConstNode[F]]
        val rangeValid = {
          if (node.begin.value >= node.end.value)
            List(FieldError(node.unit.field, s"${node.begin.value} should be less than ${node.end.value}"))
          else List.empty
        }

        subValidator.validate(node.begin) ++ subValidator.validate(node.end) ++ rangeValid
      }
  }

  implicit def severalValidator[F <: CronField](
    implicit
    ev: Enumerated[CronUnit[F]]
  ): NodeValidator[SeveralNode[F]] = new NodeValidator[SeveralNode[F]] {
    def validate(node: SeveralNode[F]): List[FieldError] = {
      def implicationErrorMsg(that: EnumerableNode[F], impliedBy: EnumerableNode[F]): String =
        s"Value '${that.shows}' at field ${that.unit.field} is implied by '${impliedBy.shows}'"

      def verifyImplication(seen: List[EnumerableNode[F]], curr: EnumerableNode[F]): Option[FieldError] = {
        val alreadyImplied = seen.find(e => curr.impliedBy(e))
          .map(found => FieldError(curr.unit.field, implicationErrorMsg(curr, found)))
        val impliesOther = seen.find(_.impliedBy(curr))
          .map(found => FieldError(curr.unit.field, implicationErrorMsg(found, curr)))

        alreadyImplied.orElse(impliesOther)
      }

      val subExprValidator = NodeValidator[EnumerableNode[F]]
      val zero = (List.empty[EnumerableNode[F]], List.empty[FieldError])
      val (_, errorResult) = node.values.foldRight(zero) { case (e, (seen, errors)) =>
        val subErrors = subExprValidator.validate(e)
        val newErrors = verifyImplication(seen, e).toList ::: subErrors ::: errors
        val newSeen = e :: seen
        newSeen -> newErrors
      }

      errorResult
    }
  }

  implicit def everyValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
    ): NodeValidator[EveryNode[F]] = new NodeValidator[EveryNode[F]] {
      def validate(node: EveryNode[F]): List[FieldError] = {
        val baseErrors = NodeValidator[DivisibleNode[F]].validate(node.base)
        val evenlyDivided = (node.base.range.size % node.freq) == 0
        if (!evenlyDivided) {
          baseErrors :+ FieldError(
            node.unit.field,
            s"Step '${node.freq}' does not evenly divide the value '${node.base.shows}' in field ${node.unit}"
          )
        } else baseErrors
      }
    }

}

private[validation] trait LowPriorityNodeValidatorInstances {

  implicit def enumerableNodeValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
    ): NodeValidator[EnumerableNode[F]] = new NodeValidator[EnumerableNode[F]] {
      def validate(node: EnumerableNode[F]): List[FieldError] =
        node.raw.fold(ops.validate)
    }

  implicit def divisibleNodeValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
    ): NodeValidator[DivisibleNode[F]] = new NodeValidator[DivisibleNode[F]] {
      def validate(node: DivisibleNode[F]): List[FieldError] =
        node.raw.fold(ops.validate)
    }

  implicit def fieldNodeValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
    ): NodeValidator[FieldNode[F]] = new NodeValidator[FieldNode[F]] {
      def validate(node: FieldNode[F]): List[FieldError] =
        node.raw.fold(ops.validate)
    }
}
