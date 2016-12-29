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

  def validate(expr: A): List[FieldError]

}

object NodeValidator extends NodeValidatorInstances {

  @inline def apply[A](implicit validator: NodeValidator[A]): NodeValidator[A] = validator

}

private[validation] trait NodeValidatorInstances extends LowPriorityNodeValidatorInstances {

  implicit def eachValidator[F <: CronField]: NodeValidator[EachNode[F]] =
    new NodeValidator[EachNode[F]] {
      def validate(expr: EachNode[F]): List[FieldError] = List.empty
    }

  implicit def constValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
    ): NodeValidator[ConstNode[F]] = new NodeValidator[ConstNode[F]] {

      def validate(expr: ConstNode[F]): List[FieldError] = {
        if (expr.value < expr.unit.min || expr.value > expr.unit.max) {
          List(FieldError(
            expr.unit.field,
            s"Value ${expr.value} is out of bounds for field: ${expr.unit.field}"
          ))
        } else List.empty
      }

    }

  implicit def betweenValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
    ): NodeValidator[BetweenNode[F]] = new NodeValidator[BetweenNode[F]] {
      def validate(expr: BetweenNode[F]): List[FieldError] = {
        val subValidator = NodeValidator[ConstNode[F]]
        val rangeValid = {
          if (expr.begin.value >= expr.end.value)
            List(FieldError(expr.unit.field, s"${expr.begin.value} should be less than ${expr.end.value}"))
          else List.empty
        }

        subValidator.validate(expr.begin) ++ subValidator.validate(expr.end) ++ rangeValid
      }
  }

  implicit def severalValidator[F <: CronField](
    implicit
    ev: Enumerated[CronUnit[F]]
  ): NodeValidator[SeveralNode[F]] = new NodeValidator[SeveralNode[F]] {
    def validate(expr: SeveralNode[F]): List[FieldError] = {
      def implicationErrorMsg(that: SeveralMemberNode[F], impliedBy: SeveralMemberNode[F]): String =
        s"Value '${that.shows}' at field ${that.unit.field} is implied by '${impliedBy.shows}'"

      def verifyImplication(seen: List[SeveralMemberNode[F]], curr: SeveralMemberNode[F]): Option[FieldError] = {
        val alreadyImplied = seen.find(e => curr.impliedBy(e))
          .map(found => FieldError(curr.unit.field, implicationErrorMsg(curr, found)))
        val impliesOther = seen.find(_.impliedBy(curr))
          .map(found => FieldError(curr.unit.field, implicationErrorMsg(found, curr)))

        alreadyImplied.orElse(impliesOther)
      }

      val subExprValidator = NodeValidator[SeveralMemberNode[F]]
      val zero = (List.empty[SeveralMemberNode[F]], List.empty[FieldError])
      val (_, errorResult) = expr.values.foldRight(zero) { case (e, (seen, errors)) =>
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
      def validate(expr: EveryNode[F]): List[FieldError] =
        NodeValidator[FrequencyBaseNode[F]].validate(expr.value)
    }

}

private[validation] trait LowPriorityNodeValidatorInstances {

  implicit def severalMemberNodeValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
    ): NodeValidator[SeveralMemberNode[F]] = new NodeValidator[SeveralMemberNode[F]] {
      def validate(expr: SeveralMemberNode[F]): List[FieldError] =
        expr.fold(ops.validate)
    }

  implicit def frequencyBaseNodeValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
    ): NodeValidator[FrequencyBaseNode[F]] = new NodeValidator[FrequencyBaseNode[F]] {
      def validate(expr: FrequencyBaseNode[F]): List[FieldError] =
        expr.fold(ops.validate)
    }

  implicit def fieldNodeValidator[F <: CronField](
      implicit
      ev: Enumerated[CronUnit[F]]
    ): NodeValidator[FieldNode[F]] = new NodeValidator[FieldNode[F]] {
      def validate(expr: FieldNode[F]): List[FieldError] =
        expr.fold(ops.validate)
    }
}
