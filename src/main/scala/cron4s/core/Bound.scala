package cron4s.core

import simulacrum.typeclass

/**
  * Created by alonsodomin on 31/12/2015.
  */
@typeclass trait Bound[T] {

  def min: T
  def max: T

}
