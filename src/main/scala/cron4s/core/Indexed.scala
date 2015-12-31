package cron4s.core

import simulacrum.typeclass

/**
  * Created by domingueza on 31/12/15.
  */
@typeclass trait Indexed[T] {

  def apply(index: Int): Option[T]

  def indexOf(item: T): Option[Int]

}
