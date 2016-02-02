package com.github.tayama0324.dora

import com.github.tayama0324.scalajs.webaudio.AudioBuffer
import com.github.tayama0324.scalajs.webaudio.AudioBufferSourceNode
import com.github.tayama0324.scalajs.webaudio.AudioNode
import com.github.tayama0324.scalajs.webaudio.AudioProcessingEvent
import com.github.tayama0324.scalajs.webaudio.EndedEvent
import org.scalajs.dom
import org.scalajs.dom.DOMException
import org.scalajs.dom.Event
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.HTMLInputElement
import scala.collection.mutable.ArrayBuffer
import scala.scalajs.concurrent.JSExecutionContext
import scala.scalajs.js
import scala.scalajs.js.typedarray.Float32Array
import scala.util.control.NonFatal

class Part(volumeControl: HTMLInputElement) {
  val bufferSize = 4096

  var buffer: Seq[ArrayBuffer[Float32Array]] = Seq()
  var processorNode: Option[AudioNode] = None
  var replayNode: Option[AudioNode] = None
  var player: Option[Player] = None

  def startRecord() = {
    if (processorNode.isDefined) {
      throw new RuntimeException("Already under recording.")
    }

    val numberOfChannel = Global.getMicrophoneSourceNode.numberOfOutputs
    val node = Global.getAudioContext.createScriptProcessor(
      bufferSize,
      numberOfChannel,
      1 // TODO: Maybe we want to use numberOfChannel
    )

    buffer = Seq.fill(numberOfChannel)(ArrayBuffer())

    val onAudioProcess: js.Function1[AudioProcessingEvent, Unit] = { e: AudioProcessingEvent =>
      println("processing...")
      (0 until e.inputBuffer.numberOfChannels).foreach { ch =>
        val in = e.inputBuffer.getChannelData(ch)
        val out = buffer(ch)
        out += new Float32Array(in)
      }
    }
    node.onaudioprocess = onAudioProcess
    Global.getMicrophoneSourceNode.connect(node)
    node.connect(Global.getAudioContext.destination)
    processorNode = Some(node)
  }

  def stopRecord(): Unit = {
    processorNode match {
      case Some(node) =>
        node.disconnect()
        processorNode = None
      case None =>
        throw new RuntimeException("This part is not under recording.")
    }
  }

  def prepareForReplay() = {
    stopReplay()

    val totalLength = buffer.head.map(_.length).sum
    val numberOfChannel = buffer.length
    val outputBuffer = Global.getAudioContext.createBuffer(
      numberOfChannel,
      totalLength,
      Global.getAudioContext.sampleRate
    )

    0 until numberOfChannel foreach { ch =>
      val channel = outputBuffer.getChannelData(ch)
      var outIndex = 0
      buffer(ch).foreach { array =>
        channel.set(array, outIndex)
        outIndex += array.length
      }
    }

    val p = new Player(outputBuffer, volumeControl, { _: EndedEvent => () })
    p.prepareForReplay()
    player = Some(p)
  }

  def startReplay(): Unit = {
    if (player.isEmpty) {
      println("Warining: Call prepareForReplay first.")
      prepareForReplay()
    }

    player.foreach(_.play(0))
  }

  def stopReplay(): Unit = {
    player match {
      case Some(p) =>
        p.stop()
        player = None
      case None =>
    }
  }
}

class Player(buffer: AudioBuffer, volumeControl: HTMLInputElement, onEnded: js.Function1[EndedEvent, Unit]) {
  var sourceNode: Option[AudioBufferSourceNode] = None

  def prepareForReplay(): Unit = {
    stop()
    val source = Global.getAudioContext.createBufferSource()
    sourceNode = Some(source)
    source.buffer = buffer

    val gain = Global.getAudioContext.createGain()
    val setGain = { () =>
      val max = volumeControl.max.toFloat
      val min = volumeControl.min.toFloat
      val value = volumeControl.valueAsNumber.toFloat
      val factor = (value - min) / max
      gain.gain.value = factor * factor
    }
    setGain()
    volumeControl.onchange = { _: Event => setGain() }

    source.onended = onEnded
    source.connect(gain)
    gain.connect(Global.getDestination)
    gain.connect(Global.getAudioContext.destination)
  }

  def play(offset: Float): Unit = {
    if (sourceNode.isEmpty) {
      println("Warning: Call prepareForPlay first.")
      this.prepareForReplay()
    }
    sourceNode match {
      case Some(node) => dom.window.setTimeout({ () => node.start() }, offset)
        println("Channel: " + node.numberOfOutputs)
      case None => println("Error: Not not prepared yet.")
    }
  }

  def stop(): Unit = {
    sourceNode match {
      case Some(node) =>
        node.stop()
        node.disconnect()
        sourceNode = None
      case None =>
        println("not playing")
    }
  }
}

class Piano(filename: String, startSecond: Double, endSecond: Double, fadeSecond: Double) {
  private var buffer: Option[AudioBuffer] = None
  private var player: Option[Player] = None

  private val replayOffset = 180
  private implicit val executionContext = JSExecutionContext.runNow

  load()

  def load(): Unit = {
    println("Loading " + filename)
    Ajax.get(filename, responseType = "arraybuffer").map { xhr: XMLHttpRequest =>
      val response = xhr.response.asInstanceOf[js.typedarray.ArrayBuffer]
      Global.getAudioContext.decodeAudioData(
        response,
        { decoded: AudioBuffer => buffer = Some(clip(decoded)) },
        { e: DOMException => println("Error with decoding audio data: " + e) }
      )
    }.recover {
      case NonFatal(e) =>
        e.printStackTrace()
    }
  }

  def clip(data: AudioBuffer): AudioBuffer = {
    val sampleRate = Global.getAudioContext.sampleRate
    val start = (startSecond * sampleRate).toInt
    val end = (endSecond * sampleRate).toInt

    val buf = Global.getAudioContext.createBuffer(
      data.numberOfChannels, end - start, sampleRate
    )
    0 until data.numberOfChannels foreach { ch =>
      val bufferChannel = buf.getChannelData(ch)
      data.copyFromChannel(bufferChannel, ch, start)

      val fadeLength = (fadeSecond * sampleRate).toInt
      val fadeIn = fadeLength
      val fadeOut = buf.length - fadeLength
      0 until fadeIn foreach { i =>
        val factor = i.toFloat / fadeLength
        val e = bufferChannel.get(i)
        bufferChannel.set(i, e * factor * factor)
      }
      fadeOut until buf.length foreach { i =>
        val j = i - fadeOut
        val factor = (1.0 - (j.toFloat / fadeLength)).toFloat
        val e = bufferChannel.get(i)
        bufferChannel.set(i, e * factor * factor)
      }
    }
    buf
  }

  def prepareForReplay(volumeControl: HTMLInputElement, onEnded: EndedEvent => Unit): Unit = {
    stopReplay()
    player = buffer.map(new Player(_, volumeControl, onEnded))
    player.foreach(_.prepareForReplay())
  }

  def startReplay(offset: Float): Unit = {
    player match {
      case Some(p) =>
        p.play(offset)
      case None =>
    }
  }

  def stopReplay() = {
    player.foreach(_.stop())
    player = None
  }
}
