package test

import akka.actor.{Actor, ActorSystem}
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterAll, WordSpec}

import scala.EventSource.{UnregisterListener, RegisterListener}

/**
 * Created by stroumphs on 10/3/2014.
 */
class TestEventSource extends Actor with ProductionEventSource{
    override def receive: Receive = eventSourceReceive
}

class EventSourceSpec extends TestKit(ActorSystem("EventSourceSpec")) with WordSpec with MustMatchers with BeforeAndAfterAll{
    override protected def afterAll(): Unit = system.shutdown()

    "EventSource" should {
        "allow us to register a listener" in {
            val real = TestActorRef[TestEventSource].underlyingActor
            real.receive(RegisterListener(testActor))
            real.listeners must contain (testActor)
        }

        "allow us to unregister a listener" in {
            val real = TestActorRef[TestEventSource].underlyingActor
            real.receive(RegisterListener(testActor))
            real.receive(UnregisterListener(testActor))
            real.listeners.size must be (0)
        }

        "send the event to our test actor" in {
            val testA = TestActorRef[TestEventSource]
            testA ! RegisterListener(testActor)
            testA.underlyingActor.sendEvent("Fibonacci")
            expectMsg("Fibonacci")
        }
    }
}
