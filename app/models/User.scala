package models

import play.api.libs.json._

/**
  * Created by asrulsibaoel on 26/02/17.
  */
case class User(name: String) {
  def isAdmin: Boolean = (name.toLowerCase() == "admin")

}

object User {
  implicit val userFormat = Json.format[User]
}
