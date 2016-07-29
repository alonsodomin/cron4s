package cron4s.expr

import cron4s.types.MonoidK
import cron4s.matcher._

/**
  * Created by domingueza on 29/07/2016.
  */
abstract class RichCronExprBase[DateTime : FieldExtractor](expr: CronExpr) {

  def matcher(implicit M: MonoidK[Matcher]): Matcher[DateTime] = {
    val functions = new MatcherPolyFuns[DateTime]
    import functions._

    expr.repr.map(exprToMatcher).foldLeft(M.empty[DateTime])(combine)
  }

  def forAll: Matcher[DateTime] =
    matcher(Matcher.disjunction.monoid)

  def exists: Matcher[DateTime] =
    matcher(Matcher.conjunction.monoid)

}
