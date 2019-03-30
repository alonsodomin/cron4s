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
package syntax

private[syntax] trait CronStringSyntax {

  implicit def toCronStringInterpolator(sc: StringContext): CronStringInterpolator =
    new CronStringInterpolator(sc)

}

final class CronStringInterpolator(val sc: StringContext) extends AnyVal {
  def cron(args: Any*): CronExpr = {
    val literals = sc.parts.iterator
    val holes    = args.iterator
    val buf      = new StringBuffer(literals.next)

    while (literals.hasNext) {
      buf.append(holes.next)
      buf.append(literals.next)
    }

    Cron.unsafeParse(buf.toString)
  }
}

object string extends CronStringSyntax
