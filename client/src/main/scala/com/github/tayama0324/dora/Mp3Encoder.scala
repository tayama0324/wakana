package com.github.tayama0324.dora

import com.github.tayama0324.dora.Mp3Encoder.WavFile
import org.scalajs.dom.Blob
import org.scalajs.dom.FileReader
import org.scalajs.dom.UIEvent
import org.scalajs.dom.Worker
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer
import scala.scalajs.js.typedarray.Float32Array
import scala.scalajs.js.typedarray.Uint8Array

/**
 * Created by takashi_tayama on 2016/01/17.
 */
class Mp3Encoder(mp3WorkerPath: String) {

  private def parseWav(buffer: Uint8Array): WavFile = {
    def readInt(start: Int, length: Int): Int = {
      val bytes = start until (start + length) map { i => buffer.get(i) }
      bytes.reverse.foldLeft(0) { (z, a) => (z << 8) | a }
    }
    require(readInt(20, 2) == 1, "Compression code must be PCM.")
    require(readInt(22, 2) == 1, "The number of channels must be 1.")
    WavFile(
      sampleRate = readInt(24, 4),
      bitsPerSample = readInt(34, 2),
      samples = buffer.subarray(44)
    )
  }

  private def toFloat32Array(array: Uint8Array): Float32Array = {
    val result = new Float32Array(array.length / 2)
    0 until result.length foreach { i =>
      val value = array.get(i * 2) + (array.get(i * 2 + 1) << 8)
      val negated = if (value >= 0x8000) -value else value
      result.set(i, negated.toFloat / 0x8000)
    }
    result
  }

  def encode(wav: Blob, callback: Blob => Unit): Unit = {
    val fileReader = new FileReader()
    fileReader.onload = { e: UIEvent =>
      val arrayBuffer = fileReader.result.asInstanceOf[ArrayBuffer]
      val buffer = new Uint8Array(arrayBuffer)
      val wavFile = parseWav(buffer)
      val encoderWorker = new Worker(mp3WorkerPath)

      encoderWorker.postMessage(js.Dynamic.literal(
        cmd = "init",
        config = js.Dynamic.literal(
          mode = 3,
          channels = 1,
          samplerate = wavFile.sampleRate,
          bitrate = wavFile.bitsPerSample
        )
      ))
      encoderWorker.postMessage(js.Dynamic.literal(
        cmd = "encode",
        buf = toFloat32Array(wavFile.samples)
      ))
      encoderWorker.postMessage(js.Dynamic.literal(
        cmd = "finish"
      ))

      encoderWorker.onmessage = { eAny: Any =>
        val e: js.Dynamic = eAny.asInstanceOf[js.Dynamic]

        e.data.cmd.asInstanceOf[String] match {
          case "data" =>
            val mp3Blob = new Blob(js.Array(new Uint8Array(e.data.buf.asInstanceOf[ArrayBuffer])))
            callback(mp3Blob)
        }
      }
    }

    fileReader.readAsArrayBuffer(wav)
  }
}

object Mp3Encoder {
  case class WavFile(sampleRate: Int, bitsPerSample: Int, samples: Uint8Array)
}