package com.github.tayama0324.scalajs.webaudio

import org.scalajs.dom.DOMException
import org.scalajs.dom.Event
import org.scalajs.dom.raw.HTMLMediaElement
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer
import scala.scalajs.js.typedarray.Float32Array
import scala.scalajs.js.typedarray.Uint8Array

/**
 * The AudioContext interface represents an audio-processing graph
 * built from audio modules linked together, each represented by an AudioNode.
 * An audio context controls both the creation of the nodes it contains and
 * the execution of the audio processing, or decoding.
 * You need to create an AudioContext before you do anything else,
 * as everything happens inside a context.
 *
 * An AudioContext can be a target of events, therefore it implements the EventTarget interface.
 */
@js.native
class AudioContext extends EventTarget {

  // Properties ----

  /**
   * Returns a double representing an ever-increasing hardware time
   * in seconds used for scheduling. It starts at 0.
   */
  def currentTime: Double = js.native

  /**
   * Returns an AudioDestinationNode representing the final destination of
   * all audio in the context. It can be thought of as the audio-rendering device.
   * @return
   */
  def destination: AudioDestinationNode = js.native

  /**
   * Returns the AudioListener object, used for 3D spatialization.
   */
  def listener: AudioListener = js.native

  /**
   * Returns a float representing the sample rate (in samples per second)
   * used by all nodes in this context. The sample-rate of an AudioContext cannot be changed.
   */
  val sampleRate: Float = js.native

  /**
   * Returns the current state of the AudioContext.
   */
  def state: String = js.native

  /**
   * An event handler that runs when an event of type statechange has fired.
   * This occurs when the AudioContext's state changes, due to the calling of
   * one of the state change methods
   * (AudioContext.suspend, AudioContext.resume, or AudioContext.close.)
   */
  var onstatechange: js.Function0[Unit] = js.native


  // Methods ----

  /**
   * Closes the audio context, releasing any system audio resources that it uses.
   */
  def close(): Promise[Unit] = js.native

  /**
   * Creates a new, empty AudioBuffer object, which can then be populated by
   * data and played via an AudioBufferSourceNode.
   *
   * @param numOfChannels An integer representing the number of channels this buffer should have.
   *                      Implementations must support a minimum 32 channels.
   * @param length An integer representing the size of the buffer in sample-frames.
   * @param sampleRate The sample-rate of the linear audio data in sample-frames per second.
   *                   An implementation must support sample-rates in at least the range 22050 to 96000.
   * @return An AudioBuffer.
   */
  def createBuffer(numOfChannels: Int, length: Int, sampleRate: Float): AudioBuffer = js.native

  /**
   * Creates an AudioBufferSourceNode, which can be used to play and manipulate
   * audio data contained within an AudioBuffer object.
   * AudioBuffers are created using AudioContext.createBuffer or
   * returned by AudioContext.decodeAudioData when it successfully decodes an audio track.
   *
   * @return An AudioBufferSourceNode.
   */
  def createBufferSource(): AudioBufferSourceNode = js.native

  /**
   * Creates a MediaElementAudioSourceNode associated with an HTMLMediaElement.
   * This can be used to play and manipulate audio from <video> or <audio> elements.
   *
   * @param myMediaElement An HTMLMediaElement object that you want to feed into
   *                       an audio processing graph to manipulate.
   * @return A MediaElementAudioSourceNode.
   */
  def createMediaElementSource(myMediaElement: HTMLMediaElement): MediaElementAudioSourceNode =
    js.native

  /**
   * Creates a MediaStreamAudioSourceNode associated with a MediaStream representing
   * an audio stream which may come from the local computer microphone or other sources.
   * @param stream A MediaStream object that you want to feed into
   *               an audio processing graph to manipulate.
   * @return A MediaStreamAudioSourceNode.
   */
  def createMediaStreamSource(stream: MediaStream): MediaStreamAudioSourceNode = js.native

  /**
   * Creates a MediaStreamAudioDestinationNode associated with a MediaStream representing
   * an audio stream which may be stored in a local file or sent to another computer.
   * @return A MediaStreamAudioDestinationNode.
   */
  def createMediaStreamDestination(): MediaStreamAudioDestinationNode = js.native

  /**
   * Creates a ScriptProcessorNode, which can be used for direct audio processing via JavaScript.
   *
   * @param bufferSize The buffer size in units of sample-frames.
   *                   If specified, the bufferSize must be one of the following values:
   *                   256, 512, 1024, 2048, 4096, 8192, 16384. If it's not passed in,
   *                   or if the value is 0, then the implementation will choose
   *                   the best buffer size for the given environment,
   *                   which will be a constant power of 2 throughout the lifetime of the node.
   *                   This value controls how frequently the audioprocess event is dispatched
   *                   and how many sample-frames need to be processed each call.
   *                   Lower values for bufferSize will result in a lower (better) latency.
   *                   Higher values will be necessary to avoid audio breakup and glitches.
   *                   It is recommended for authors to not specify this buffer size and allow
   *                   the implementation to pick a good buffer size to balance
   *                   between latency and audio quality.
   * @param numberOfInputChannels Integer specifying the number of channels for this node's input,
   *                              defaults to 2. Values of up to 32 are supported.
   * @param numberOfOutputChannels Integer specifying the number of channels for this node's output,
   *                               defaults to 2. Values of up to 32 are supported.
   * @return A ScriptProcessorNode.
   */
  def createScriptProcessor(
    bufferSize: Int,
    numberOfInputChannels: Int,
    numberOfOutputChannels: Int
  ): ScriptProcessorNode = js.native

  /**
   * Creates a StereoPannerNode, which can be used to apply stereo panning to an audio source.
   *
   * @return A StereoPannerNode.
   */
  def createStereoPanner(): StereoPannerNode = js.native

  /**
   * Creates an AnalyserNode, which can be used to expose audio time and frequency data
   * and for example to create data visualisations.
   *
   * @return An AnalyserNode.
   */
  def createAnalyser(): AnalyserNode = js.native

  // TODO: define following facades.
  //  AudioContext.createBiquadFilter()
  //  Creates a BiquadFilterNode, which represents a second order filter configurable as several different common filter types: high-pass, low-pass, band-pass, etc.
  //  AudioContext.createChannelMerger()
  //  Creates a ChannelMergerNode, which is used to combine channels from multiple audio streams into a single audio stream.
  //  AudioContext.createChannelSplitter()
  //  Creates a ChannelSplitterNode, which is used to access the individual channels of an audio stream and process them separately.
  //  AudioContext.createConvolver()
  //  Creates a ConvolverNode, which can be used to apply convolution effects to your audio graph, for example a reverberation effect.
  //  AudioContext.createDelay()
  //  Creates a DelayNode, which is used to delay the incoming audio signal by a certain amount. This node is also useful to create feedback loops in a Web Audio API graph.
  //  AudioContext.createDynamicsCompressor()
  //  Creates a DynamicsCompressorNode, which can be used to apply acoustic compression to an audio signal.

  /**
   * Creates a GainNode, which can be used to control the overall volume of the audio graph.
   * @return A GainNode
   */
  def createGain(): GainNode = js.native

  //  AudioContext.createOscillator()
  //  Creates an OscillatorNode, a source representing a periodic waveform. It basically generates a tone.
  //  AudioContext.createPanner()
  //  Creates a PannerNode, which is used to spatialise an incoming audio stream in 3D space.
  //  AudioContext.createPeriodicWave()
  //  Creates a PeriodicWave, used to define a periodic waveform that can be used to determine the output of an OscillatorNode.
  //  AudioContext.createWaveShaper()
  //  Creates a WaveShaperNode, which is used to implement non-linear distortion effects.
  //  AudioContext.createAudioWorker()
  //  Creates an AudioWorkerNode, which can interact with a web worker thread to generate, process, or analyse audio directly. This was added to the spec on August 29 2014, and is not implemented in any browser yet.
  //  AudioContext.decodeAudioData()
  //
  /**
   * Asynchronously decodes audio file data contained in an ArrayBuffer.
   * In this case, the ArrayBuffer is usually loaded from an XMLHttpRequest's
   * response attribute after setting the responseType to arraybuffer.
   * This method only works on complete files, not fragments of audio files.
   * @param arrayBuffer An ArrayBuffer containing the audio data to be decoded,
   *                    usually grabbed from an XMLHttpRequest's response attribute
   *                    after setting the responseType to arraybuffer.
   * @param decodeSuccessCallback A callback function to be invoked when the decoding
   *                              successfully finishes. The single argument to this
   *                              callback is an AudioBuffer representing the decoded PCM
   *                              audio data. Usually you'll want to put the decoded data
   *                              into an AudioBufferSourceNode, from which it can be played
   *                              and manipulated how you want.
   * @param decodeFailureCallback An optional error callback, to be invoked if an error occurs
   *                              when the audio data is being decoded.
   */
  def decodeAudioData(
    arrayBuffer: ArrayBuffer,
    decodeSuccessCallback: js.Function1[AudioBuffer, Unit],
    decodeFailureCallback: js.Function1[DOMException, Unit]
  ): Promise[AudioBuffer] = js.native
  //  AudioContext.resume()
  //  Resumes the progression of time in an audio context that has previously been suspended.
  //  AudioContext.suspend()
  //  Suspends the progression of time in the audio context, temporarily halting audio hardware access and reducing CPU/battery usage in the process.
}

/**
 * The AudioBuffer interface represents a short audio asset residing in memory,
 * created from an audio file using the AudioContext.decodeAudioData() method,
 * or from raw data using AudioContext.createBuffer(). Once put into an AudioBuffer,
 * the audio can then be played by being passed into an AudioBufferSourceNode.
 *
 * Objects of these types are designed to hold small audio snippets, typically
 * less than 45 s. For longer sounds, objects implementing the MediaElementAudioSourceNode
 * are more suitable. The buffer contains data in the following format:
 * non-interleaved IEEE754 32-bit linear PCM with a nominal range between -1 and +1,
 * that is, 32bits floating point buffer, with each samples between -1.0 and 1.0.
 * If the AudioBuffer has multiple channels, they are stored in separate buffer.
 */
@js.native
trait AudioBuffer extends js.Object {
  // Properties ----

  /**
   * Returns a float representing the sample rate, in samples per second,
   * of the PCM data stored in the buffer.
   */
  val sampleRate: Float = js.native

  /**
   * Returns an integer representing the length, in sample-frames,
   * of the PCM data stored in the buffer.
   */
  val length: Int = js.native

  /**
   * Returns a double representing the duration, in seconds,
   * of the PCM data stored in the buffer.
   */
  val duration: Double = js.native

  /**
   * Returns an integer representing the number of discrete audio channels
   * described by the PCM data stored in the buffer.
   */
  val numberOfChannels: Int = js.native

  // Methods ----
  /**
   * Returns a Float32Array containing the PCM data associated with the channel,
   * defined by the channel parameter (with 0 representing the first channel).
   *
   * @param channel The channel property is an index representing the particular channel
   *                to get data for. An index value of 0 represents the first channel.
   *                If the channel index value is greater than of equal to
   *                AudioBuffer.numberOfChannels, an INDEX_SIZE_ERR exception will be thrown.
   * @return A Float32Array.
   */
  def getChannelData(channel: Int): Float32Array = js.native

  /**
   * Copies the samples from the specified channel of the AudioBuffer to the destination array.
   *
   * @param destination A Float32Array to copy the channel data to.
   * @param channelNumber The channel number of the current AudioBuffer to
   *                      copy the channel data from. If channelNumber is greater than
   *                      or equal to AudioBuffer.numberOfChannels, an INDEX_SIZE_ERR
   *                      will be thrown.
   * @param startInChannel An optional offset to copy the data from. If startInChannel is
   *                       greater than AudioBuffer.length, an INDEX_SIZE_ERR will be thrown.
   */
  def copyFromChannel(
    destination: Float32Array,
    channelNumber: Int,
    startInChannel: Int = 0
  ): Unit = js.native

  /**
   * Copies the samples to the specified channel of the AudioBuffer, from the source array.
   *
   * @param source A Float32Array that the channel data will be copied from.
   * @param channelNumber The channel number of the current AudioBuffer to copy the
   *                      channel data to. If channelNumber is greater than or equal to
   *                      AudioBuffer.numberOfChannels, an INDEX_SIZE_ERR will be thrown.
   * @param startInChannel An optional offset to copy the data to. If startInChannel is
   *                       greater than AudioBuffer.length, an INDEX_SIZE_ERR will be thrown.
   */
  def copyToChannel(
    source: Float32Array,
    channelNumber: Int,
    startInChannel: Int = 0
  ): Unit = js.native
}

@js.native
trait AudioBufferSourceNode extends AudioNode {
  // Properties ----

  /**
   * Is an AudioBuffer that defines the audio asset to be played,
   * or when set to the value null, defines a single channel of silence.
   */
  var buffer: AudioBuffer = js.native

  /**
   * Is a k-rate AudioParam representing detuning of oscillation in cents. Its default value is 0.
   */
  var detune: AudioParam = js.native

  /**
   * Is a Boolean attribute indicating if the audio asset must be replayed when
   * the end of the AudioBuffer is reached. Its default value is false.
   */
  var loop: Boolean = js.native

  /**
   * Is a double value indicating, in seconds, where in the AudioBuffer
   * the restart of the play must happen. Its default value is 0.
   */
  var loopStart: Double = js.native

  /**
   * Is a double value indicating, in seconds, where in the AudioBuffer
   * the replay of the play must stop (and eventually loop again).
   * Its default value is 0.
   */
  var loopEnd: Double = js.native

  /**
   * Is an a-rate AudioParam that defines the speed factor at which
   * the audio asset will be played. Since no pitch correction is
   * applied on the output, this can be used to change the pitch of the sample.
   */
  var playbackRate: AudioParam = js.native

  /**
   * Is an EventHandler containing the callback associated with the ended event.
   */
  var onended: js.Function1[EndedEvent, Unit] = js.native

  // Methods ----

  /**
   * Schedules the start of the playback of the audio asset.
   *
   * @param when The when parameter defines when the play will start.
   *             If when represents a time in the past, the play will start immediately.
   *             If the method is called more than one time, or after a call to
   *             AudioBufferSourceNode.stop(), an exception is raised.
   * @param offset The offset parameter, which defaults to 0, defines where
   *               the playback will start.
   * @param duration The duration parameter, which defaults to the length of
   *                 the asset minus the value of offset, defines the length of
   *                 the portion of the asset to be played.
   */
  def start(when: Double = 0, offset: Double = 0, duration: Double = 0): Unit = js.native

  /**
   * Schedules the end of the playback of an audio asset.
   *
   * @param when when The when parameter defines when the playback will stop.
   *             If it represents a time in the past, the playback will end immediately.
   *             If this method is called twice or more, an exception is raised.
   */
  def stop(when: Double = 0): Unit = js.native
}

@js.native
trait GainNode extends AudioNode {

  /**
   * Is an a-rate AudioParam representing the amount of gain to apply.
   */
  val gain: AudioParam
}

@js.native
trait EndedEvent extends Event

@js.native
trait MediaElementAudioSourceNode extends AudioNode

/**
 * EventTarget is an interface implemented by objects that
 * can receive events and may have listeners for them.
 *
 * Element, document, and window are the most common event targets,
 * but other objects can be event targets too, for example
 * XMLHttpRequest, AudioNode, AudioContext, and others.
 *
 * Many event targets (including elements, documents, and windows) also
 * support setting event handlers via on... properties and attributes.
 */
@js.native
trait EventTarget extends js.Object {
  /**
   * The EventTarget.addEventListener() method registers the specified listener
   * on the EventTarget it's called on. The event target may be an Element in a document,
   * the Document itself, a Window, or any other object that
   * supports events (such as XMLHttpRequest).
   *
   * @param type A string representing the event type to listen for.
   * @param listener The object that receives a notification when an event of
   *                 the specified type occurs. This must be an object implementing
   *                 the EventListener interface, or simply a JavaScript function.
   * @param useCapture If true, useCapture indicates that the user wishes to initiate capture.
   *                   After initiating capture, all events of the specified type will be
   *                   dispatched to the registered listener before being dispatched to
   *                   any EventTarget beneath it in the DOM tree.
   *                   Events that are bubbling upward through the tree will not trigger
   *                   a listener designated to use capture.
   *                   Event bubbling and capturing are two ways of propagating events that
   *                   occur in an element that is nested within another element,
   *                   when both elements have registered a handle for that event.
   *                   The event propagation mode determines the order in which
   *                   elements receive the event.
   *                   See DOM Level 3 Events and JavaScript Event order for
   *                   a detailed explanation.
   *                   If not specified, useCapture defaults to false.
   */
  def addEventListener(
    `type`: String,
    listener: EventListener,
    useCapture: Boolean
  ): Unit = js.native

  /**
   * The EventTarget.addEventListener() method registers the specified listener
   * on the EventTarget it's called on. The event target may be an Element in a document,
   * the Document itself, a Window, or any other object that
   * supports events (such as XMLHttpRequest).
   *
   * @param type A string representing the event type to listen for.
   * @param listener The object that receives a notification when an event of
   *                 the specified type occurs. This must be an object implementing
   *                 the EventListener interface, or simply a JavaScript function.
   * @param useCapture If true, useCapture indicates that the user wishes to initiate capture.
   *                   After initiating capture, all events of the specified type will be
   *                   dispatched to the registered listener before being dispatched to
   *                   any EventTarget beneath it in the DOM tree.
   *                   Events that are bubbling upward through the tree will not trigger
   *                   a listener designated to use capture.
   *                   Event bubbling and capturing are two ways of propagating events that
   *                   occur in an element that is nested within another element,
   *                   when both elements have registered a handle for that event.
   *                   The event propagation mode determines the order in which
   *                   elements receive the event.
   *                   See DOM Level 3 Events and JavaScript Event order for
   *                   a detailed explanation.
   *                   If not specified, useCapture defaults to false.
   */
  def addEventListener(
    `type`: String,
    listener: Event => Unit,
    useCapture: Boolean
  ): Unit = js.native

  /**
   * Removes the event listener previously registered with EventTarget.addEventListener().
   *
   * @param type A string representing the event type to remove.
   * @param listener The EventListener function to remove from the event target.
   * @param useCapture Specifies whether the EventListener to be removed is registered as
   *                   a capturing listener or not.
   *                   If this parameter is absent, a default value of false is assumed.
   *                   If a listener is registered twice, one with capture and one without,
   *                   remove each one separately. Removal of a capturing listener does not
   *                   affect a non-capturing version of the same listener, and vice versa.
   */
  def removeEventListener(
    `type`: String,
    listener: EventListener,
    useCapture: Boolean
  ): Unit = js.native

  /**
   * Dispatches an Event at the specified EventTarget, invoking the affected EventListeners
   * in the appropriate order. The normal event processing rules (including the capturing and
   * optional bubbling phase) apply to events dispatched manually with dispatchEvent().
   * @param event is the Event object to be dispatched.
   * @return The return value is false if at least one of the event handlers
   *         which handled this event called Event.preventDefault(). Otherwise it returns true.
   */
  def dispatchEvent(event: Event): Boolean = js.native
}

/**
 * This method is called whenever an event occurs of the type
 * for which the EventListener interface was registered.
 */
@js.native
trait EventListener extends js.Object {
  def handleEvent(event: Event): Unit = js.native
}

@js.native
trait AudioNode extends EventTarget {
  // Properties ----

  /**
   * Returns the associated AudioContext, that is the object representing
   * the processing graph the node is participating in.
   */
  val context: AudioContext = js.native

  /**
   * Returns the number of inputs feeding the node. Source nodes are defined as
   * nodes having a numberOfInputs property with a value of 0.
   */
  val numberOfInputs: Int = js.native

  /**
   * Returns the number of outputs coming out of the node.
   * Destination nodes — like AudioDestinationNode — have a value of 0 for this attribute.
   */
  val numberOfOutputs: Int = js.native

  /**
   * Represents an integer used to determine how many channels are used
   * when up-mixing and down-mixing connections to any inputs to the node.
   * Its usage and precise definition depend on the value of AudioNode.channelCountMode.
   */
  def channelCount: Int = js.native

  /**
   * Represents an enumerated value describing the way channels must be
   * matched between the node's inputs and outputs.
   */
  def channelCountMode: String = js.native

  /**
   * Represents an enumerated value describing the meaning of the channels.
   * This interpretation will define how audio up-mixing and down-mixing will happen.
   *
   * The possible values are "speakers" or "discrete".
   */
  def channelInterpretation: String = js.native

  // Methods ----

  /**
   * Allows us to connect one output of this node to one input of another node.
   *
   * @param destination The AudioNode you are connecting to.
   * @param input An index describing which output of the current AudioNode
   *              you want to connect to the destination. The index numbers are
   *              defined according to the number of output channels (see Audio channels).
   *              If this parameter is out-of-bound, an INDEX_SIZE_ERR exception is thrown.
   *              It is possible to connect an AudioNode output to more than one input with
   *              multiple calls to connect(). Therefore fan-out is supported.
   * @param output An index describing which input of the destination you want to
   *               connect the current AudioNode to. The index numbers are defined
   *               according to the number of input channels (see Audio channels)
   *               If this parameter is out-of-bound, an INDEX_SIZE_ERR exception
   *               is thrown. It is possible to connect an AudioNode to another AudioNode,
   *               which in turn connects back to the first AudioNode, creating a cycle.
   *               This is allowed only if there is at least one DelayNode in the cycle.
   *               Otherwise, a NOT_SUPPORTED_ERR exception will be thrown.
   */
  def connect(destination: AudioNode, output: Int = 0, input: Int = 0): Unit = js.native

  /**
   * Allows us to connect one output of this node to one input of an audio parameter.
   */
  def connect(audioParam: AudioParam): Unit = js.native

  /**
   * Allows us to disconnect the current node from another one it is already connected to.
   *
   * @param destination A specific AudioNode to disconnect from.
   * @param output An index describing which output of the current AudioNode you want to disconnect.
   *               The index numbers are defined according to the number of output channels
   *               (see Audio channels).  If this parameter is out-of-bound,
   *               an INDEX_SIZE_ERR exception is thrown.
   * @param input An index describing which input of the given destination AudioNode
   *              you want to disconnect. The index numbers are defined according to
   *              the number of input channels (see Audio channels).  If this parameter
   *              is out-of-bound, an INDEX_SIZE_ERR exception is thrown.
   */
  def disconnect(destination: AudioNode = null, output: Int = 0, input: Int = 0): Unit = js.native
}

/**
 * The AudioParam interface represents an audio-related parameter,
 * usually a parameter of an AudioNode (such as GainNode.gain).
 * An AudioParam can be set to a specific value or a change in value,
 * and can be scheduled to happen at a specific time and following a specific pattern.
 *
 * [[https://developer.mozilla.org/en-US/docs/Web/API/AudioParam]]
 */
@js.native
trait AudioParam extends AudioNode {
  // Properties ----

  /**
   * Represents the parameter's current floating point value; initially set to the value of
   * AudioParam.defaultValue. Though it can be set, any modifications happening while
   * there are automation events scheduled — that is events scheduled using the methods of
   * the AudioParam — are ignored, without raising any exception.
   */
  var value: Float = js.native

  /**
   * Represents the initial value of the attributes as defined by the specific AudioNode
   * creating the AudioParam.
   */
  val defaultValue: Float = js.native

  // Methods ----

  /**
   */
  /**
   * Schedules an instant change to the value of the AudioParam at a precise time,
   * as measured against AudioContext.currentTime. The new value is given in the value parameter.
   *
   * @param value A floating point number representing the value
   *              the AudioParam will change to at the given time.
   * @param startTime A double representing the exact time (in seconds)
   *                  after the AudioContext was first created that
   *                  the change in value will happen.
   */
  def setValueAtTime(value: Float, startTime: Double): Unit = js.native

  /**
   * Schedules a gradual linear change in the value of the AudioParam.
   * The change starts at the time specified for the previous event,
   * follows a linear ramp to the new value given in the value parameter,
   * and reaches the new value at the time given in the endTime parameter.
   *
   * @param value A floating point number representing the value
   *              the AudioParam will ramp up to by the given time.
   * @param endTime A double representing the exact time (in seconds)
   *                after the ramping starts that the changing of the value will stop.
   */
  def linearRampToValueAtTime(value: Float, endTime: Double): Unit = js.native

  /**
   * Schedules a gradual exponential change in the value of the AudioParam.
   * The change starts at the time specified for the previous event,
   * follows an exponential ramp to the new value given in the value parameter,
   * and reaches the new value at the time given in the endTime parameter.
   *
   * @param value A floating point number representing the value
   *              the AudioParam will ramp up to by the given time.
   * @param endTime A double representing the exact time (in seconds)
   *                after the ramping starts that the changing of the value will stop.
   */
  def exponentialRampToValueAtTime(value: Float, endTime: Double): Unit = js.native

  /**
   *
   * @param target The value the parameter will start to transition towards at the given start time.
   * @param startTime The time that the exponential transition will begin,
   *                  which will be relative to AudioContext.currentTime.
   * @param timeConstant The time-constant value of first-order filter (exponential)
   *                     approach to the target value.
   *                     The larger this value is, the slower the transition will be.
   */
  def setTargetAtTime(target: Float, startTime: Double, timeConstant: Double): Unit = js.native

  /**
   * Schedules the values of the AudioParam to follow a set of values,
   * defined by the values Float32Array scaled to fit into the given interval,
   * starting at startTime, and having a specific duration.
   *
   * @param values A Float32Array representing the value curve
   *               the AudioParam will change through along the duration.
   * @param startTime A double representing the exact time (in seconds)
   *                  after the AudioContext was first created that
   *                  the change in value will happen.
   * @param duration A double representing the exact time (in seconds)
   *                 during which the values will be changed between.
   *                 The values are spaced equally along this duration.
   */
  def setValueCurveAtTime(values: Float32Array, startTime: Double, duration: Double): Unit =
    js.native

  /**
   * Cancels all scheduled future changes to the AudioParam.
   *
   * @param startTime A double representing the exact time (in seconds)
   *                  after the AudioContext was first created after
   *                  which all scheduled changes will be cancelled.
   */
  def cancelScheduledValues(startTime: Double): Unit = js.native
}

@js.native
trait AudioDestinationNode extends AudioNode {
  /**
   * Is an unsigned long defining the maximum amount of channels
   * that the physical device can handle.
   */
  val maxChannelCount: Long = js.native
}

@js.native
trait AudioListener extends AudioNode {
  /**
   * Defines the orientation of the listener.
   *
   * @param x The x value of the front vector of the listener.
   * @param y The y value of the front vector of the listener.
   * @param z The z value of the front vector of the listener.
   * @param xUp The x value of the up vector of the listener.
   * @param yUp The y value of the up vector of the listener.
   * @param zUp The z value of the up vector of the listener.
   */
  def setOrientation(x: Float, y: Float, z: Float, xUp: Float, yUp: Float, zUp: Float): Unit =
    js.native
}

@js.native
trait MediaStream extends EventTarget {

  type EventHandler = js.Function0[Unit]

  // Properties ----

  /**
   * A Boolean value that returns true if the MediaStream is active, or false otherwise.
   */
  def active: Boolean = js.native

  /**
   * Is a Boolean value set to true if the ended event has been fired on the object,
   * meaning that the stream has been completely read, or false if the end of the stream
   * has not been reached.
   */
  def ended: Boolean = js.native

  /**
   * Is a DOMString containing 36 characters denoting a
   * universally unique identifier (UUID) for the object.
   */
  val id: String = js.native

  // Event handlers ----
  /**
   * Is an EventHandler containing the action to perform
   * when an active event is fired when a MediaStream object becomes active.
   */
  var onactive: EventHandler = js.native

  /**
   * Is an EventHandler containing the action to perform when
   * an addtrack event is fired when a new MediaStreamTrack object is added.
   */
  var onaddtrack: EventHandler = js.native

  /**
   * Is an EventHandler containing the action to perform when
   * an ended event is fired when the streaming is terminating.

   */
  var onended: EventHandler = js.native

  /**
   * Is an EventHandler containing the action to perform when
   * an inactive event is fired when a MediaStream object becomes inactive.
   */
  var oninactive: EventHandler = js.native

  /**
   * Is an EventHandler containing the action to perform when
   * an removetrack event is fired when a  MediaStreamTrack object is removed from it.
   */
  var onremovetrack: EventHandler = js.native

  // Methods ----
  // Method facades are left undefined due to lack of documentation.
}


@js.native
trait MediaStreamAudioSourceNode extends AudioNode

@js.native
trait MediaStreamAudioDestinationNode extends AudioNode {
  /**
   * Is a MediaStream containing a single AudioMediaStreamTrack
   * with the same number of channels as the node itself.
   * You can use this property to get a stream out of the
   * audio graph and feed it into another construct,
   * such as a Media Recorder.
   */
  def stream: MediaStream = js.native
}

@js.native
trait ScriptProcessorNode extends AudioNode {

  type EventHandler = js.Function1[AudioProcessingEvent, Unit]

  // Properties ----
  /**
   * Returns an integer representing both the input and output buffer size.
   * Its value can be a power of 2 value in the range 256–16384.
   */
  val bufferSize: Int = js.native

  /**
   * Represents the EventHandler to be called.
   */
  var onaudioprocess: EventHandler
}

@js.native
trait AudioProcessingEvent extends Event {

  /**
   * The time when the audio will be played, as defined by the time of AudioContext.currentTime
   */
  val playbackTime: Double = js.native

  /**
   * The buffer containing the input audio data to be processed.
   * The number of channels is defined as a parameter, numberOfInputChannels,
   * of the factory method AudioContext.createScriptProcessor().
   * Note the the returned AudioBuffer is only valid in the scope of the onaudioprocess function.
   */
  val inputBuffer: AudioBuffer = js.native

  /**
   * The buffer where the output audio data should be written.
   * The number of channels is defined as a parameter, numberOfOutputChannels,
   * of the factory method AudioContext.createScriptProcessor().
   * Note the the returned AudioBuffer is only valid in the scope of the onaudioprocess function.
   */
  val outputBuffer: AudioBuffer = js.native
}


@js.native
trait StereoPannerNode extends AudioNode {
  /**
   * Is an a-rate AudioParam representing the amount of panning to apply.
   */
  val pan: AudioParam = js.native
}


@js.native
trait AnalyserNode extends AudioNode {
  // Properties ----

  /**
   * Is an unsigned long value representing the size of the FFT
   * (Fast Fourier Transform) to be used to determine the frequency domain.
   */
  var fftSize: Long = js.native

  /**
   * Is an unsigned long value half that of the FFT size.
   * This generally equates to the number of data values
   * you will have to play with for the visualization.
   */
  val frequencyBinCount: Long = js.native

  /**
   * Is a double value representing the minimum power value in the scaling range
   * for the FFT analysis data, for conversion to unsigned byte values — basically,
   * this specifies the minimum value for the range of results when using
   * getByteFrequencyData().
   */
  var minDecibels: Double = js.native

  /**
   * Is a double value representing the maximum power value in the scaling range
   * for the FFT analysis data, for conversion to unsigned byte values — basically,
   * this specifies the maximum value for the range of results when using
   * getByteFrequencyData().
   */
  var maxDecibels: Double = js.native

  /**
   * Is a double value representing the averaging constant with the last
   * analysis frame — basically, it makes the transition between values
   * over time smoother.
   */
  var smoothingTimeConstant: Double = js.native

  // Methods ----

  /**
   * Copies the current frequency data into a Float32Array array passed into it.
   *
   * @param array The Float32Array that the frequency domain data will be copied to.
   * @return A Float32Array.
   */
  def getFloatFrequencyData(array: Float32Array): Float32Array = js.native

  /**
   * Copies the current frequency data into a Uint8Array (unsigned byte array) passed into it.
   *
   * @param array The Uint8Array that the frequency domain data will be copied to.
   * @return A Uint8Array
   */
  def getByteFrequencyData(array: Uint8Array): Uint8Array = js.native

  /**
   * Copies the current waveform, or time-domain, data into a Float32Array array passed into it.
   *
   * @param array The Float32Array that the time domain data will be copied to.
   * @return A Float32Array.
   */
  def getFloatTimeDomainData(array: Float32Array): Float32Array = js.native

  /**
   * Copies the current waveform, or time-domain, data into a Uint8Array (unsigned byte array) passed into it.
   *
   * @param array The Uint8Array that the time domain data will be copied to.
   * @return A Uint8Array.
   */
  def getByteTimeDomainData(array: Uint8Array): Uint8Array = js.native
}

/**
 * Javascript Promise. Note that this is completely different thing to scala.concurrent.Promise.
 *
 * [[https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise]]
 *
 * TODO: implement methods.
 */
@js.native
trait Promise[+T] extends js.Object
