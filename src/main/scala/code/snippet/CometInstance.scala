package code
package snippet

import net.liftweb._
import common._
import http._
import util._

import scala.xml.NodeSeq

object CometActorName extends RequestVar(Helpers.nextFuncName)

/**
 * This snippet will both rewrite your comet actor to be correctly
 * named and give you access to the comet actor
 */
object CometInstance {
  /**
   * modify the snippet to do the right thing as a named comet actor
   */
  def render(in: NodeSeq): NodeSeq = {
    CometActorName.is // touch
    S.attr("type") match {
      case Full(typ) => 
        <lift:comet type={typ} name={CometActorName.is}>{in}</lift:comet>
      case _ => 
        <div>Hey... your CometInstance tag didn't include a type attribute</div>
    }
  }
    
  /**
   * Send a message to the current comet actor
   */
  def sendMessageToActor(typ: String, message: Any) {
    sendMessageToActor(typ, CometActorName, message)
  }

  /**
   * Send a message to a named comet actor.
   */
  def sendMessageToActor(typ: String, name: String, message: Any) {
    for {
      session <- S.session
    } {
      session.findComet(typ, Full(name)) match {
        case Full(a) => a ! message
        case _ =>
          session.setupComet(typ, Full(name), message)
      }
    }
  }
}

/**
 * Vend an Ajax text box
 */
object TextBox {
  def render = {
    // capture the function the sends the message to the comet actor
    val func = (a: Any) => CometInstance.sendMessageToActor("Checker",
                                                            CometActorName, a)

    import Helpers._
    import lib._
    import js.JsCmds._
    import js.JE._

    SHtml.text("", 
               s => {
                 func(UserData(s)) // send the message
                 SetValById("inthing", "") // update the UI
               }) %
    ("id" -> "inthing")
  }
}

