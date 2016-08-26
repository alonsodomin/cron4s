package cron4s

import cron4s.types.Predicate
import cron4s.types.syntax.PredicateSyntax

/**
  * Created by domingueza on 29/07/2016.
  */
package object matcher extends PredicateSyntax {
  @deprecated("Use cron4s.types.Predicate instead", "0.2.0")
  type Matcher[A] = Predicate[A]
}
