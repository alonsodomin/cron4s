package cron4s.matcher

import simulacrum.typeclass

/**
  * Created by domingueza on 04/07/2016.
  */
@typeclass trait Matchable[E, B] {

  def matches(e: E)(b: B): Boolean

}
