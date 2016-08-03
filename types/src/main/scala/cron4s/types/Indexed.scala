package cron4s.types

/**
  * Created by domingueza on 31/12/15.
  */
trait Indexed[T] {

  def apply(index: Int): Option[T]

  def indexOf(item: T): Option[Int]

}

object Indexed {
  @inline def apply[T](implicit ev: Indexed[T]): Indexed[T] = ev

  implicit def vectorInstance[T](vector: Vector[T]): Indexed[T] = new Indexed[T] {
    override def indexOf(item: T): Option[Int] = {
      val index = vector.indexOf(item)
      if (index >= 0) Some(index)
      else None
    }

    override def apply(index: Int): Option[T] = {
      if (index < 0 || index >= vector.size) None
      else Some(vector(index))
    }
  }

}