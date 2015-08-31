package com.ratrecommends.gdx

sealed trait AppEvent

object AppEvent {

  object Disposed extends AppEvent

  object Paused extends AppEvent

  object Resumed extends AppEvent

  object Created extends AppEvent

}
