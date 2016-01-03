package cron4s.expr

/**
  * Created by alonsodomin on 02/01/2016.
  */
trait Matcher[A] extends (A => Boolean) { self =>

  def comap[B](f: B => A): Matcher[B] = Matcher { b => self(f(b)) }

}

object Matcher {

  def apply[A](f: A => Boolean): Matcher[A] = new Matcher[A] {
    def apply(a: A): Boolean = f(a)
  }

}
