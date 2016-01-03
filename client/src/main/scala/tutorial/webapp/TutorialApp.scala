package tutorial.webapp

import com.github.tayama0324.dora.Global
import com.github.tayama0324.scalajs.webaudio.AudioContext
import com.github.tayama0324.scalajs.webaudio.MediaStream
import com.github.tayama0324.scalajs.webaudio.getUserMedia
import org.scalajs.dom.raw.DOMException
import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExportAll

/**
 * Created by tayama on 15/09/29.
 */
object TutorialApp {

  var count = 0

  val doc: Dynamic = Dynamic.global.document

  def printIsReady() = {
    println("ready? " + Global.isReady)
    println(Global.getMicrophoneSourceNode)
  }


}
