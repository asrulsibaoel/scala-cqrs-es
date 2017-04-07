package models

import play.api.libs.json._

/**
  * Created by asrulsibaoel on 26/02/17.
  */
case class User(name: String, password: String) {
  def isAdmin: Boolean = (name.toLowerCase() == "admin" && password.toLowerCase() == "admin123" )

  def isGuest: Boolean = (name.isEmpty)
}

object User {
  implicit val userFormat = Json.format[User]
}
