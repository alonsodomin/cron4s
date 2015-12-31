package cron4s.expr

/**
  * Created by domingueza on 31/12/15.
  */
sealed trait SpecialChar {
  def toChar: Char
}
object SpecialChar {
  case object Always extends SpecialChar {
    val toChar = '*'
  }
  case object Last extends SpecialChar {
    val toChar = 'L'
  }
}
