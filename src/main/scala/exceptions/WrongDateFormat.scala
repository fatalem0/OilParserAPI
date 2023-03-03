package exceptions

case class WrongDateFormat(message: String) extends Throwable(message)
