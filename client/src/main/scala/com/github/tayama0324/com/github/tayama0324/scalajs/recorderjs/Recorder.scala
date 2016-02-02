package com.github.tayama0324.com.github.tayama0324.scalajs.recorderjs

import com.github.tayama0324.scalajs.webaudio.AudioNode
import org.scalajs.dom.Blob
import scala.scalajs.js
import scala.scalajs.js.typedarray.Float32Array

/**
 * https://github.com/mattdiamond/Recorderjs
 *
 * A plugin for recording/exporting the output of Web Audio API nodes
 *
 * @param source The node whose output you wish to capture
 * @param config (*optional*) A configuration object
 *               workerPath - Path to recorder.js worker script. Defaults to
 *                 'js/recorderjs/recorderWorker.js'
 *               bufferLen - The length of the buffer that the internal JavaScriptNode
 *                 uses to capture the audio. Can be tweaked if experiencing performance issues.
 *                 Defaults to 4096.
 *               callback - A default callback to be used with `exportWAV`.
 *               type - The type of the Blob generated by `exportWAV`. Defaults to 'audio/wav'.

 */
@js.native
class Recorder(source: AudioNode, config: js.Object = js.Dynamic.literal()) extends js.Object {

  /**
   * begin capturing audio.
   */
  def record(): Unit = js.native

  /**
   * cease capturing audio. Subsequent calls to **record** will add to the current recording.
   */
  def stop(): Unit = js.native

  /**
   * This will clear the recording.
   */
  def clear(): Unit = js.native

  /**
   * This will generate a Blob object containing the recording in WAV format.
   * The callback will be called with the Blob as its sole argument.
   * If a callback is not specified, the default callback (as defined in the config) will be used.
   * If no default has been set, an error will be thrown.
   *
   * In addition, you may specify the type of Blob to be returned (defaults to 'audio/wav').
   */
  def exportWAV(callback: js.Function1[Blob, Unit], `type`: String = "audio/wav"): Unit = js.native

  /**
   * This will pass the recorded stereo buffer (as an array of two Float32Arrays,
   * for the separate left and right channels) to the callback. It can be played back by creating
   * a new source buffer and setting these buffers as the separate channel data:
   */
  def getBuffer(callback: js.Function1[js.Array[Float32Array], Unit]) = js.native

  /**
   * This will set the configuration for Recorder by passing in a config object.
   */
  def configure(config: js.Object) = js.native
}

/**
 * Utility Methods (static)
 */
@js.native
object Recorder extends js.Object {

  /**
   * This method will force a download using the new anchor link *download* attribute.
   * Filename defaults to 'output.wav'.
   */
  def forceDownload(blob: Blob, filename: String = "output.wav"): Unit = js.native
}
