package controllers

import akka.util.Timeout
import domain.command.CategoryCommand.Category
import domain.command.CommandRoot.Command
import global.Global
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, Controller, Result}
import play.api.libs.json.Json
import domain.handler.CategoryCommandHandler

import scala.concurrent.Future
/**
  * Created by asrulsibaoel on 18/02/17.
  */
object Categories extends Controller{
  import CategoryCommandHandler._

  implicit val categoryToJson = Json.writes[Category]

  val noName = Future.successful(BadRequest(error("please set the 'name' field")))

  def create = Action.async (parse.json) { request =>
    val name = (request.body \ "name").asOpt[String]

    name.fold(noName) { c =>
      sendCmd(AddCategoryCmd(c)) map {
        case cat: Category => Created(Json.toJson(cat))
      }
    }
  }

  val categoryAggregateManager = Global.system.actorOf(CategoryCommandHandler.props)

  def sendCmd(cmd: Command): Future[Any] = {
    import akka.pattern.ask
    import scala.concurrent.duration._

    implicit val timeout = Timeout(3.seconds)

    val result = ask(categoryAggregateManager, cmd)
    result
  }
}


