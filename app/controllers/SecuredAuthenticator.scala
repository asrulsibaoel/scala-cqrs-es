package controllers

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result
import common.Authentication
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future
/**
  * Created by asrulsibaoel on 23/02/17.
  */
case class User(email: String, userId: String)
case class UserRequest[A](user: User, request: Request[A]) extends WrappedRequest(request)

class SecuredAuthenticator extends Controller {

  implicit val formatUserDetails = Json.format[User]

  object JWTAuthentication extends ActionBuilder[UserRequest] {
    def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]): Future[SimpleResult] = {
      implicit val req = request

      val jwtToken = request.headers.get("token").getOrElse("")

      if (Authentication.isValidToken(jwtToken)) {
        Authentication.decodePayload(jwtToken).fold(
          Future.successful(Unauthorized("Invalid credential"))
        ) { payload =>
          val userInfo = Json.parse(payload).validate[User].get

          // Check decoded credentials
          if (userInfo.email == "test@example.com" && userInfo.userId == "userId123") {
            Future.successful(Ok("Authorization successful"))
          } else {
            Future.successful(Unauthorized("Invalid credential"))
          }
        }
      }
      else {
        Future.successful(Unauthorized("Invalid credential"))
      }
    }
  }

}
object SecuredAuthenticator extends SecuredAuthenticator