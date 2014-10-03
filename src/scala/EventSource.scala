package scala

import akka.actor.{Actor, ActorRef}



/**
 * Created by stroumphs on 10/1/2014.
 */

object EventSource {
    case class RegisterListener(listener: ActorRef)
    case class UnregisterListener(listener: ActorRef)
}

trait EventSource {
    def sendEvent[T](event: T): Unit
    def eventSourceReceive: Actor.Receive
}

trait ProductionEventSource extends EventSource {
    this : Actor =>
    import scala.EventSource._
    var listeners = Vector.empty[ActorRef]

    def sendEvent[T](event: T) = listeners foreach { _ ! event}

    def eventSourceReceive: Receive = {
        case RegisterListener(listener) =>
            listeners = listeners :+ listener
        case UnregisterListener(listener) =>
            listeners = listeners filter( _ != listener)
    }
}
