package scala

import akka.actor.{Actor, ActorRef}

import scala.Altimeter.RateChange
import scala.ControlSurfaces.{StickForward, StickBack}

/**
 * Created by stroumphs on 9/29/2014.
 */
object ControlSurfaces {
    case class StickBack(amount: Float)
    case class StickForward(amount: Float)
}

class ControlSurfaces(altimeter: ActorRef) extends Actor {
    override def receive: Receive = {
        case StickBack(amount) => altimeter ! RateChange(amount)
        case StickForward(amount) => altimeter ! RateChange(1 * amount)
    }
}