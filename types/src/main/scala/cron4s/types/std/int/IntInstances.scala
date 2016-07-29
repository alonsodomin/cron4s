package cron4s.types.std.int

import cron4s.types.Equal

/**
  * Created by alonsodomin on 29/07/2016.
  */
private[std] trait IntInstances {

  implicit val intInstance = new Equal[Int] {
    override def eqv(x: Int, y: Int): Boolean = x == y
  }

}
