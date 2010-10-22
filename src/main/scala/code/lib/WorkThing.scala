package code
package lib

import net.liftweb._
import common._
import actor._
import util._
import Helpers._

/**
 * This Actor updates a random subset of the actors every 5 seconds
 * If the actor sends a message, that message is reflected back to the
 * actor and schedule the actor for an extra status update
 */
object WorkThing extends LiftActor {
  private def ping() {
    ActorPing.schedule(() => this ! Tick, 1 seconds)
  }

  ping()

  private var who: Map[LiftActor, Int] = Map()

  protected def messageHandler = {
    case Tick =>
      who.keys.filter(k => Helpers.randomInt(100) > 50).foreach(test)
      ping()

    case a: LiftActor => 
      who += a -> 1; a ! Progress(1)
    
    case (a: LiftActor, UserData(s)) =>
      a ! Data(s)
      test(a)
  }

  // this is just junky random stuff that reflects
  // some background task
  private def test(a: LiftActor) {
    for {
      i <- who.get(a) if Helpers.randomInt(100) > 30
    } {
      val next = i + 1
      a ! Progress(next)
      if (next >= 100) {
        who -= a
      } else {
        who += (a -> next)
      }
    }
  }

  private case object Tick
}

case class UserData(str: String)
case class Progress(v: Int)
case class Data(str: String)

