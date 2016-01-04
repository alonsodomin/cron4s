package cron4s.core

/**
  * Created by alonsodomin on 03/01/2016.
  */
trait Similarity[A, B] {

  def same(a: A, b: B): Boolean

}
