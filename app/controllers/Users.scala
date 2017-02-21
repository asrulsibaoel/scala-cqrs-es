package controllers

import akka.util.Timeout
import common.VersionConflict
import domain.command.CommandRoot.{Command, Removed, State, Uninitialized}
import domain.command.UserCommand.User
import global.Global
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, Result}
import domain.handler.UserCommandHandler

import scala.concurrent.Future

object Users extends Controller {
  import UserCommandHandler._

  implicit val userToJson = Json.writes[User]


  val noEmail = Future.successful(BadRequest(error("please set the 'email' field")))
  val conflict: PartialFunction[Any, Result] = {
    case VersionConflict(id, expectedVersion, receivedVersion) =>
      Conflict(error(s"version conflict for id '$id'. Expected=$expectedVersion. Received=$receivedVersion"))
  }


  def add = Action.async(parse.json) { request =>
    val email = (request.body \ "email").asOpt[String]

    email.fold(noEmail) { e =>
      sendCmd(AddUserCmd(e)) map {
        case u: User => Created(Json.toJson(u))
      }
    }
  }


  def get(id: String) = Action.async {
    sendCmd(GetUserCmd(id)) map {
      case u: User => Ok(Json.toJson(u))
      case Uninitialized | Removed => NotFound(error(s"user '$id' not found"))
    }
  }


  def delete(id: String, version: Long) = Action.async {
    sendCmd(DeleteUserCmd(id, version)) map {
      conflict orElse {
        case _ => NoContent
      }
    }
  }


  def update(id: String, version: Long) = Action.async(parse.json) { request =>
    val email = (request.body \ "email").asOpt[String]

    email.fold(noEmail) { e =>
      sendCmd(UpdateEmailCmd(id, e, version)) map {
        conflict orElse {
          case u: User => Created(Json.toJson(u))
        }
      }
    }
  }


  val userAggregateManager = Global.system.actorOf(UserCommandHandler.props)

  def sendCmd(cmd: Command): Future[Any] = {
    import akka.pattern.ask
    import scala.concurrent.duration._

    implicit val timeout = Timeout(3.seconds)

    val result = ask(userAggregateManager, cmd)
    result
  }

}
