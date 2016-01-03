package com.github.tayama0324.scalajs.webaudio

import org.scalajs.dom.raw.DOMException
import scala.scalajs.js

/**
 * Facades for adapter.js [[https://github.com/webrtc/adapter/blob/master/adapter.js]].
 */
@js.native
object getUserMedia extends js.Object {
  def apply(
    constraints: js.Object,
    onSuccess: js.Function1[MediaStream, Unit],
    onError: js.Function1[DOMException, Unit]
  ): Unit = js.native
}
