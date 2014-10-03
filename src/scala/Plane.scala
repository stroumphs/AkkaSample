package scala

import akka.actor.{Actor, ActorLogging, Props}

import scala.Altimeter.AltitudeUpdate
import scala.EventSource.RegisterListener
import scala.Plane.GiveMeControl

/**
 * Created by stroumphs on 9/29/2014.
 */
object Plane {
    case class GiveMeControl
}

class Plane extends Actor with ActorLogging {

    val altimeter = context.actorOf(Props[Altimeter])
    val controls = context.actorOf(Props(new ControlSurfaces(altimeter)))

    override def receive: Receive = {
        case AltitudeUpdate(altitude) =>
            log.info(s"Altitude is now: $altitude")
        case GiveMeControl =>
            log.info("Plane giving control.")
            sender ! controls
    }

    override def preStart(): Unit = altimeter ! RegisterListener(self)
}
