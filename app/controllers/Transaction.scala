package controllers

import domain.command.CommandRoot.Command
import domain.command.UserCommand.User
import domain.handler.UserCommandHandler.AddUserCmd
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future


object Transaction extends Controller {

  val noProductFound = Future.successful("");

  def inputMerchant(productId : String)  = Action.async(parse.json) { request =>

    val productId = (request.body \ "product_id").asOpt[String]

    productId.fold(noProductFound) { e =>
      sendCmd(AddUserCmd(e)) map {
        case u: User => Created(Json.toJson(u))
      }
    }
  }

  /**
    * it will return my cardinal number which i must set it first!
    * @param command
    * @return
    */
  def sendCmd(command : Command): Future[Any] = {

  }
}

