package cron4s.core

/**
  * Created by alonsodomin on 03/01/2016.
  */
object Isomorphism {

  trait Iso[Arr[_, _], A, B] {
    def to: Arr[A, B]
    def from: Arr[B, A]
  }

  type IsoSet[A, B] = Iso[Function1, A, B]

  type <=>[A, B] = IsoSet[A, B]

}
