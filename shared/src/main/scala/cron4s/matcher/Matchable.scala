package cron4s.matcher

import simulacrum.typeclass

/**
  * Created by domingueza on 04/07/2016.
  */
@typeclass trait Matchable[A] {

  def matches[B](a: A)(b: B): Boolean

}
