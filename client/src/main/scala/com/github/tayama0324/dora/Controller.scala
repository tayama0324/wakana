package com.github.tayama0324.dora

import com.github.tayama0324.com.github.tayama0324.scalajs.recorderjs.Recorder
import com.github.tayama0324.scalajs.webaudio.AudioContext
import com.github.tayama0324.scalajs.webaudio.AudioNode
import com.github.tayama0324.scalajs.webaudio.EndedEvent
import com.github.tayama0324.scalajs.webaudio.MediaStream
import com.github.tayama0324.scalajs.webaudio.getUserMedia
import java.util.concurrent.atomic.AtomicBoolean
import org.scalajs.dom.Blob
import org.scalajs.dom.Event
import org.scalajs.dom.FileReader
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.UIEvent
import org.scalajs.dom.raw.DOMException
import org.scalajs.dom.raw.HTMLInputElement
import org.scalajs.dom.raw.WebSocket
import org.scalajs.jquery.jQuery
import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.typedarray.Float32Array


case class PianoConfig(
  filename: String,
  start: Int,
  end: Int,
  fade: Int
)

@JSExportAll
class Controller {
  type MutableMap[K, V] = scala.collection.mutable.Map[K, V]

  val pianoConfigs = Map(
    "kokoro-no-senritsu-middle" -> PianoConfig(
      "assets/blob/piano/kokoro-no-senritsu.mp3", 55, 78, 2
    )
  )
  val impulseResponses = Map(
    "parth-hall" -> new ImpulseResponse("assets/blob/ir/perth_city_hall_balcony_ir_edit.wav"),
    "central-hall" -> new ImpulseResponse("assets/blob/ir/ir_row_1l_sl_centre.wav")
  )


  var piano: Option[Piano] = None
  var parts: MutableMap[String, Part] = new scala.collection.mutable.HashMap

  def initialize(): Unit = {
    stop()

    val selection = jQuery("#pianoSelect").`val`().asInstanceOf[String]
    val pianoConfig = pianoConfigs.getOrElse(selection, throw new RuntimeException("Not found."))
    piano = Some(new Piano(
      pianoConfig.filename,
      pianoConfig.start,
      pianoConfig.end,
      pianoConfig.fade
    ))
    impulseResponses.values.foreach(_.load())
  }

  def playPiano(): Unit = {
    val volumeControl = js.Dynamic.global.document.getElementById("piano-volume")
      .asInstanceOf[HTMLInputElement]
    val onEnded = { _: EndedEvent => () }
    piano match {
      case Some(p) =>
        p.prepareForReplay(volumeControl, onEnded)
        p.startReplay(0)
      case None =>
        throw new RuntimeException("Piano is not loaded.")
    }
  }

  def stop(): Unit = {
    piano.foreach(_.stopReplay())
    parts.values.foreach(_.startReplay())
  }

  def appendPart(): Unit = {
    appendPartInternal(jQuery("#part-name").`val`().asInstanceOf[String])
    js.Dynamic.global.document.getElementById("part-name").value = ""
  }

  def appendPartInternal(name: String): Unit = {
    val document = js.Dynamic.global.document
    val volume = document.createElement("input").asInstanceOf[HTMLInputElement]
    volume.`type` = "range"
    volume.min = "0"
    volume.max = "100"
    volume.value = "100"
    val part = new Part(volume)

    val recordButton = document.createElement("button")
    recordButton.onclick = { () => record(name) }
    recordButton.innerText = "Record"

    val replayButton = document.createElement("button")
    replayButton.onclick = { () => part.startReplay() }
    replayButton.innerText = "Replay"

    val dumpButton = document.createElement("button")
    dumpButton.onclick = { () => dumpPart(name) }
    dumpButton.innerText = "Download"

    val table = document.getElementById("table")
    val row = table.insertRow(-1)

    row.insertCell(-1).innerText = name
    row.insertCell(-1).appendChild(volume)
    val cell = row.insertCell(-1)
    cell.appendChild(recordButton)
    cell.appendChild(dumpButton)

    parts.update(name, part)
  }

  def record(partName: String): Unit = {
    val part = parts.getOrElse(
      partName, throw new RuntimeException(s"Part `$partName` is undefined.")
    )
    val p = piano.getOrElse(throw new RuntimeException("Piano is undefined."))
    val pianoVolumeControl =
      js.Dynamic.global.document.getElementById("piano-volume").asInstanceOf[HTMLInputElement]
    val onEnded = { _: EndedEvent => part.stopRecord() }
    p.prepareForReplay(pianoVolumeControl, onEnded)
    p.startReplay(0)
    part.startRecord()
  }

  def dumpPart(name: String): Unit = {
    println("Not implemented yet.")
    //    Controller.prototype.dumpPart = function(partName) {
    //      var obj = {}
    //      var part = this.parts[partName];
    //      obj[partName] = part.buffer;
    //      var data = JSON.stringify(obj);
    //      var blob = new Blob([data], { type: "text/plain" });
    //      var a = document.createElement("a");
    //      a.href = URL.createObjectURL(blob);
    //      a.target = "_blank";
    //      a.download = partName + ".json";
    //      a.click();
    //    };
  }

  def onFileSelected(event: Event): Unit = {
    val fileList = event.target.asInstanceOf[HTMLInputElement].files
    val files = 0 until fileList.length map { fileList.item }
    files.foreach { file =>
      val reader = new FileReader
      reader.onload = { e: UIEvent =>
        val obj = JSON.parse(reader.result.toString)
          .asInstanceOf[js.Dictionary[js.Array[js.Array[js.Array[Float]]]]]
        obj.foreach { case (partName, rawBuffer) =>
          println("Reading file for part " + partName)
          appendPartInternal(partName)
          parts(partName).buffer = rawBuffer.toSeq.map { arrays =>
            val b = ArrayBuffer.empty[Float32Array]
            arrays.foreach { array =>
              b += new Float32Array(array)
            }
            b
          }
        }
      }
      reader.readAsText(file)
    }
  }

  def replayAll(): Unit = {
    val recorder = Global.getRecorder

    parts.values.foreach(_.prepareForReplay())
    piano.foreach(_.prepareForReplay(
      js.Dynamic.global.document.getElementById("piano-volume").asInstanceOf[HTMLInputElement],
      { _: EndedEvent =>
        recorder.stop()
        recorder.getBuffer({ buffers: js.Array[Float32Array] =>
          val left = buffers(0)
          val right = buffers(1)
          val blob = Global.newMp3Encoder().encodeDirectly(left, right)
          Recorder.forceDownload(blob, "output-direct.mp3")
        })
//        recorder.exportWAV({ wav: Blob =>
//          Global.newMp3Encoder().encode(wav, { mp3: Blob =>
//            Recorder.forceDownload(mp3, "output.mp3")
//          })
//        })
      }
    ))
    recorder.clear()
    recorder.record()
    parts.values.foreach(_.startReplay())
    piano.foreach(_.startReplay(180))
  }
}

@JSExportAll
object Global extends js.JSApp {
  private val audioContext = new AudioContext
  private var sourceNode: Option[AudioNode] = None
  private val ready: AtomicBoolean = new AtomicBoolean(false)
  private val destination = audioContext.createGain()
  private val recorder: Recorder = new Recorder(destination, js.Dynamic.literal(
    workerPath = "/assets/js/bower_components/recorderjs/recorderWorker.js"
  ))
  //  def newMp3Encoder(): Mp3Encoder = new Mp3Encoder(
  //    "/assets/js/Recordmp3js/js/mp3worker.js"
  //  )
  def newMp3Encoder(): LameJsMp3Encorder = new LameJsMp3Encorder(
    workerPath = "/assets/js/node_modules/lamejs/worker/worker.js",
    config = js.Dynamic.literal()
  )

  val controller: Controller = new Controller

  destination.gain.value = 0.5F

  def initialize(): Unit = {
    getUserMedia(
      js.Dynamic.literal(audio = true, video = false),
      { localMediaStream: MediaStream =>
        sourceNode = Some(audioContext.createMediaStreamSource(localMediaStream))
        ready.set(true)
      },
      { e: DOMException => println(e) }
    )

    controller.initialize()
    js.Dynamic.global.document.getElementById("upload")
      .addEventListener("change", { e: Event =>
        controller.onFileSelected(e)
    })
  }

  def main(): Unit = {}
  def isReady: Boolean = ready.get()

  def getAudioContext: AudioContext = audioContext

  def getMicrophoneSourceNode: AudioNode = {
    sourceNode match {
      case Some(node) if isReady => node
      case None => throw new RuntimeException("Microphone is not ready yet.")
    }
  }

  def getRecorder = recorder
  def getDestination = destination
}
