package com.github.tayama0324.dora

import org.scalajs.dom.Blob
import org.scalajs.dom.UIEvent
import org.scalajs.dom.Worker
import org.scalajs.dom.raw.FileReader
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer
import scala.scalajs.js.typedarray.Float32Array
import scala.scalajs.js.typedarray.Int16Array
import scala.scalajs.js.typedarray.Int8Array
import scala.scalajs.js.typedarray.Uint8Array


/**
 * lamejs-based MP3 encoder.
 */
class LameJsMp3Encorder(workerPath: String, config: js.Object) {

  import js.Dynamic.{ global => g }
  val worker = new Worker(workerPath)

  def encodeDirectly(left: Float32Array, right: Float32Array): Blob = {
    val lame = js.Dynamic.newInstance(g.lamejs)()
    val encoder = js.Dynamic.newInstance(lame.Mp3Encoder)(1, 44100, 256)
    val lefts = toInt16Arrays(left)
    val rights = toInt16Arrays(right)
    val result = js.Array[js.Any]()

    0 until lefts.length foreach { i =>
      result.push(encoder.encodeBuffer(lefts(i), rights(i))).asInstanceOf[Int8Array]
    }
    result.push(encoder.flush().asInstanceOf[Int8Array])

    new Blob(result)
  }

  def toInt16Arrays(array: Float32Array): js.Array[Int16Array] = {
    val length = array.length
    val chunkSize = 1152
    val chunkCount = (length + chunkSize - 1) / chunkSize
    val result = js.Array[Int16Array]()
    0 until chunkCount foreach { _ => result.push(new Int16Array(chunkSize)) }
    0 until length foreach { i =>
      val v = array.get(i)
      val u = if (v < 0) v * 32768 else v * 32767
      result(i / chunkSize).set(i % chunkSize, u.toShort)
    }
    result
  }

  def encode(wav: Blob, callback: Blob => Unit): Unit = {
    val reader = new FileReader()
    reader.onload = { e: UIEvent =>
      val wavBytes = reader.result.asInstanceOf[ArrayBuffer]
      g.console.log("wavBytes", new Uint8Array(wavBytes))
      g.console.log("length: ", wavBytes.byteLength)

      worker.onmessage = { eAny: Any =>
        val e: js.Dynamic = eAny.asInstanceOf[js.Dynamic]

        e.data.cmd.asInstanceOf[String] match {
          case "end" =>
            val mp3Blob = new Blob(e.data.buf.asInstanceOf[js.Array[js.Any]])
            callback(mp3Blob)
          case "progress" => println("Converting to MP3...")
          case "error" => println("An error occurred.\n" + e.toString)
        }
      }
      worker.postMessage(js.Dynamic.literal(
        cmd = "init",
        config = config
      ))
      worker.postMessage(js.Dynamic.literal(
        cmd = "encode",
        rawInput = wavBytes
      ))
      worker.postMessage(js.Dynamic.literal(
        cmd = "finish"
      ))
    }
    
    reader.readAsArrayBuffer(wav)
  }
}

/**

(function (exports) {
  'use strict';

  var MP3Converter = function (config) {
    config = config || {};
    var busy = false;
    var mp3Worker = new Worker('worker.js');

    this.isBusy = function () {
      return busy
    };

    this.convert = function (blob) {
      var conversionId = 'conversion_' + Date.now(),
        tag = conversionId + ":"
        ;
      var opts = [];
      for(var i=1; i < arguments.length;i++){
        opts.push(arguments[i]);
      }
      console.log(tag, 'Starting conversion');
      var preferredConfig = {}, onSuccess, onProgress, onError;
      if (typeof opts[0] == 'object') {
          preferredConfig = opts.shift();
      }


      onSuccess = opts.shift();
      onProgress = opts.shift();
      onError = opts.shift();

      if (busy) {
        throw ("Another conversion is in progress");
      }

      var initialSize = blob.size,
        fileReader = new FileReader(),
        startTime = Date.now();

      fileReader.onload = function (e) {
        console.log(tag, "Passed to BG process");
        mp3Worker.postMessage({
          cmd: 'init',
          config: preferredConfig
        });

        mp3Worker.postMessage({cmd: 'encode', rawInput: e.target.result});
        mp3Worker.postMessage({cmd: 'finish'});

        mp3Worker.onmessage = function (e) {
          if (e.data.cmd == 'end') {
            console.log(tag, "Done converting to Mp3");
            var mp3Blob = new Blob(e.data.buf, {type: 'audio/mp3'});
            console.log(tag, "Conversion completed in: " + ((Date.now() - startTime) / 1000) + 's');
            var finalSize = mp3Blob.size;
            console.log(tag +
              "Initial size: = " + initialSize + ", " +
              "Final size = " + finalSize
              + ", Reduction: " + Number((100 * (initialSize - finalSize) / initialSize)).toPrecision(4) + "%");

            busy = false;

            if(onProgress && typeof onProgress=='function'){
              onProgress(1);
            }

            if (onSuccess && typeof onSuccess === 'function') {
              onSuccess(mp3Blob);
            }
          } else if(e.data.cmd == 'progress'){
            //post progress
            if(onProgress && typeof onProgress=='function'){
              onProgress(e.data.progress);
            }
          } else if(e.data.cmd == 'error'){

          }
        };
      };
      busy = true;
      fileReader.readAsArrayBuffer(blob);
    }
  };

  exports.MP3Converter = MP3Converter;
})(window);
  */