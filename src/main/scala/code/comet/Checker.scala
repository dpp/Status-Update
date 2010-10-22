package code
package comet

import net.liftweb._
import common._
import http._
import util._
import Helpers._

import lib._

class Checker extends CometActor {
  // the CometActor should be disposed of after 
  // it doesn't appear on a page for 30 seconds
  override def lifespan = Full(30 seconds)

  private var progress = 0
  private var data: Vector[String] = Vector.empty

  /**
   * Register with the WorkThing
   */
  override def localSetup() {
    super.localSetup()
    WorkThing ! this
  }

  /**
   * Handle various messages
   */
  override def lowPriority = {
    case Progress(v) => progress = v; reRender()
    case Data(d) => data :+= d ; reRender()
    case ud@UserData(_) => WorkThing ! (this -> ud)
  }
  

  // Render the component... yeah... it should bind to content
  def render = {
    <div>
    Progress {progress}%
    {
      data.map(d => <span>{d}<br/></span>)
    }
    </div>
  }

}
