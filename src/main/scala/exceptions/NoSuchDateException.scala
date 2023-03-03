package exceptions

case class NoSuchDateException(message: String) extends Throwable(message)
