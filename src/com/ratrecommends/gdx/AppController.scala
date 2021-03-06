package com.ratrecommends.gdx

import com.badlogic.gdx.{ApplicationListener, Gdx, utils}

abstract class AppController extends ApplicationListener {

  private[this] final val states = new utils.Array[AppState]()
  private[this] final val listeners = ObjectMap[AppEvent, utils.SnapshotArray[() => Unit]]()
  private[this] final var isResumed = false
  private[this] final var previousWidth = -1
  private[this] final var previousHeight = -1

  final def addListener(event: AppEvent, listener: () => Unit) = {
    listeners.getOrElseUpdate(event, new SnapshotArray()).addIfNotContains(listener, identity = true)
  }

  final def removeListener(event: AppEvent, listener: () => Unit) = if (listeners.containsKey(event)) {
    listeners.get(event).removeValue(listener, true)
  }

  private[this] final def notify(event: AppEvent) = if (listeners.containsKey(event)) {
    listeners.get(event).foreach(_.apply())
  }

  final def state: AppState = states.peek()

  final def stateExists: Boolean = states.size > 0

  final def push(newState: AppState): Unit = {
    if (stateExists) {
      state.pause()
    }
    states.add(newState)
    newState.enter()
    if (isResumed) {
      newState.resume()
      newState.resize(previousWidth, previousHeight)
    }
  }

  final def pop(exitIfEmpty: Boolean = true): AppState = {
    val prev = states.pop()
    prev.pause()
    prev.exit()

    if (stateExists) {
      if (isResumed) {
        state.resume()
        state.resize(previousWidth, previousHeight)
      }
    } else if (exitIfEmpty) {
      Gdx.app.exit()
    }
    prev
  }

  final def replace(newState: AppState): Unit = {
    if (stateExists)
      pop(false)
    push(newState)
  }

  override final def create(): Unit = {
    isResumed = true
    created()
    notify(AppEvent.Created)
  }

  override final def resume(): Unit = {
    if (!isResumed) {
      isResumed = true
      if (stateExists) {
        state.resume()
        state.resize(previousWidth, previousHeight)
      }
    }
    notify(AppEvent.Resumed)
  }

  override final def resize(width: Int, height: Int): Unit = {
    if (width != previousWidth || height != previousHeight) {
      previousWidth = width
      previousHeight = height
      if (stateExists) {
        state.resize(width, height)
      }
    }
  }

  override final def render(): Unit = {
    if (stateExists) {
      state.render()
    }
  }

  override final def pause(): Unit = {
    if (isResumed) {
      isResumed = false
      if (stateExists) {
        state.pause()
      }
    }
    notify(AppEvent.Paused)
  }

  override final def dispose(): Unit = {
    while (stateExists) {
      pop(false)
    }
    disposed()
    notify(AppEvent.Disposed)
  }

  def created(): Unit

  def disposed(): Unit

}
