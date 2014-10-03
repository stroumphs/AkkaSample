package scala

import akka.actor.{Actor, ActorLogging}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
/**
 * Created by stroumphs on 9/29/2014.
 */
class Altimeter extends Actor with ActorLogging{
    this: EventSource =>

    val ceiling = 43000
    val maxRateOfClimb = 500

    var rateOfClimb : Float = 0

    var altitude : Double = 0

    var lastTick = System.currentTimeMillis()

    val ticker = context.system.scheduler.schedule(100.millis, 100.millis, self, Tick)

    case object Tick

    override def receive: Receive = eventSourceReceive orElse altimeterReceive

    def altimeterReceive: Receive = {
        case Altimeter.RateChange(amount) =>
            rateOfClimb = amount.min(1.0f).max(-1.0f) * maxRateOfClimb
            log.info(s"Altimeter changed rate of climb to $rateOfClimb.")
        case Tick =>
            val tick = System.currentTimeMillis()
            altitude = altitude + ((tick - lastTick) / 60000.0) * rateOfClimb
            lastTick = tick
            sendEvent(Altimeter.AltitudeUpdate(altitude))
    }

    override def postStop(): Unit = ticker.cancel
}

object Altimeter {
    case class RateChange(amount: Float)
    case class AltitudeUpdate(altitude: Double)

    def apply() = new Altimeter with ProductionEventSource
}