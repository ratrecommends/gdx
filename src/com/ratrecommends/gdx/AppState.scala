package com.ratrecommends.gdx

abstract class AppState {

  private[this] final var isEntered = false
  private[this] final var isResumed = false
  private[this] final var previousWidth = -1
  private[this] final var previousHeight = -1

  private[gdx] final def enter(): Unit = {
    if (!isEntered) {
      isEntered = true
      entered()
    }
  }

  private[gdx] final def resume(): Unit = {
    if (!isResumed) {
      isResumed = true
      resumed()
    }
  }

  private[gdx] final def resize(width: Int, height: Int): Unit = {
    if (width != previousWidth || height != previousHeight) {
      previousWidth = width
      previousHeight = height
      resized(width, height)
    }
  }

  private[gdx] final def render(): Unit = rendered()

  private[gdx] final def pause(): Unit = {
    if (isResumed) {
      isResumed = false
      paused()
    }
  }

  private[gdx] final def exit(): Unit = {
    if (isEntered) {
      isEntered = false
      exited()
    }
  }


  protected def entered(): Unit

  protected def resumed(): Unit

  protected def resized(width: Int, height: Int): Unit

  protected def rendered(): Unit

  protected def paused(): Unit

  protected def exited(): Unit

}
