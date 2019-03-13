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

import cron4s.parsing._

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.{Failure, Success, Try}

/**
  * The entry point for parsing cron expressions
  *
  * @author Antonio Alonso Dominguez
  */
@JSExportTopLevel("Cron")
object Cron {

  /**
    * Parses the given cron expression into a cron AST using Either as return type. This is a short-hand for
    * `Cron.parse(...)`
    *
    * @param e a cron expression
    * @return an Either representing the failure or the actual parsed cron AST
    * @example val cron = Cron("10-35 2,4,6 * ? * *")
    */
  def apply(e: String): Either[Error, CronExpr] = parse(e)

  /**
    * Parses the given cron expression into a cron AST using Either as return type
    *
    * @param e a cron expression
    * @return an Either representing the failure or the actual parsed cron AST
    * @example val cron = Cron.parse("10-35 2,4,6 * ? * *")
    */
  def parse(e: String): Either[Error, CronExpr] =
    parse0(e).right.flatMap(validation.validateCron)

  /**
    * Parses the given cron expression into a cron AST using Try as return type
    *
    * @param e a cron expression
    * @return a Try representing the failure or the actual parsed cron AST
    * @example val cron = Cron.tryParse("10-35 2,4,6 * ? * *")
    */
  def tryParse(e: String): Try[CronExpr] = parse(e) match {
    case Left(err)   => Failure(err)
    case Right(expr) => Success(expr)
  }

  /**
    * Parses the given cron expression into a cron AST. This method will throw an exception in case the
    * given cron expression is invalid
    *
    * @param e a cron expression
    * @return a cron AST
    * @throws Error in case the cron expression is invalid
    * @example val cron = Cron.unsafeParse("10-35 2,4,6 * ? * *")
    */
  @throws(classOf[Error])
  def unsafeParse(e: String): CronExpr = parse(e) match {
    case Left(err)   => throw err
    case Right(expr) => expr
  }

  private[this] def parse0(e: String): Either[Error, CronExpr] =
    for {
      tokens <- CronLexer.tokenize(e)
      expr   <- CronParser.read(tokens)
    } yield expr
}
