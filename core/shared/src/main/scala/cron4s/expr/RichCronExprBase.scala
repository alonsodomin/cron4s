package cron4s.expr

import cron4s.types.MonoidK
import cron4s.matcher._

/**
  * Created by domingueza on 29/07/2016.
  */
abstract class RichCronExprBase[DateTime : FieldExtractor](expr: CronExpr) {

  def matches(implicit M: MonoidK[Matcher]): Matcher[DateTime] = {
    val functions = new MatcherPolyFuns[DateTime]
    import functions._

    expr.repr.map(exprToMatcher).foldRight(M.empty[DateTime])(combine)
  }

  def forall: Matcher[DateTime] =
    matches(Matcher.conjunction)

  def exists: Matcher[DateTime] =
    matches(Matcher.disjunction)

}
