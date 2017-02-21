package domain.command

import akka.actor.Props
import akka.event.LoggingReceive
import akka.persistence.SnapshotMetadata

/**
  * Created by asrulsibaoel on 18/02/17.
  */
object CategoryCommand {
  import CommandRoot._

  //state
  case class Category(id : String, name: String) extends State

  //Commands
  case class CreateCategoryCommand(name: String) extends Command
  case class UpdateCategoryNameCommand(name: String) extends Command
  case class DeleteCategoryCommand(name: String) extends Command
  case class GetCategoryCommand(id: String) extends Command

  case class CategoryCreated(name: String) extends Event
  case class CategoryNameUpdated(name: String) extends Event
  case class CategoryDeleted(name: String) extends Event

  //props
  def props(id: String) : Props = Props()

}

class CategoryCommand(id: String) extends CommandRoot {

  import CommandRoot._
  import CategoryCommand._

  override def persistenceId = id

  override def updateState(evt: Event): Unit = evt match {
    case CategoryCreated(name) =>
      context.become(created)
      state = Category(id, name)

  }

  val initial = LoggingReceive {
    case CreateCategoryCommand(name) =>
      persist(CategoryCreated(name))(afterEventPersisted)
    case GetState =>
      respond()
    case KillAggregate =>
      context.stop(self)
  }

  val created = LoggingReceive {

    case UpdateCategoryNameCommand(name) =>
      persist(CategoryNameUpdated(name)) (afterEventPersisted)
    case DeleteCategoryCommand(name) =>
      persist(CategoryDeleted(name))(afterEventPersisted)
    case GetState =>
      respond()
    case KillAggregate =>
      context.stop(self)
  }

  val removed = LoggingReceive {
    case GetState | Remove | DeleteCategoryCommand(_) =>
      respond()
    case KillAggregate =>
      context.stop(self)
  }

  override val receiveCommand: Receive = initial
  override def restoreFromSnapshot(metadata: SnapshotMetadata, state: State) : Unit = {
    this.state = state
    state match {
      case Uninitialized => context become initial
      case Removed => context become removed
      case _:Category => context become created
    }
  }
}
