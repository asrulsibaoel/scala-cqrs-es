package domain.handler

import java.util.UUID

import akka.actor.Props
import akka.event.LoggingReceive
import domain.command.CategoryCommand

/**
  * Created by asrulsibaoel on 18/02/17.
  */
object CategoryCommandHandler {

  import domain.command.CommandRoot._

  case class AddCategoryCmd(name: String) extends Command
  case class UpdateCategoryCmd(categoryId: String, name: String) extends Command
  case class DeleteCategoryCmd(categoryId: String) extends Command
  case class GetCategoryCmd(categoryId: String) extends Command

  def props: Props = Props(new CategoryCommandHandler)
}

class CategoryCommandHandler extends CommandHandler {

  import CategoryCommandHandler._
  import CategoryCommand._

  def processCommand = LoggingReceive {
    case AddCategoryCmd(name) =>
      val id = "category-" + UUID.randomUUID().toString
      processAggregateCommand(id, CreateCategoryCommand(name))
    case GetCategoryCmd(categoryId) =>
      processAggregateCommand(categoryId, GetCategoryCommand(categoryId))
  }

  override def aggregateProps(id: String): Props = CategoryCommand.props(id)
}
