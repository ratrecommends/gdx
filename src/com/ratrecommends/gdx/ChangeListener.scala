package com.ratrecommends.gdx

import com.badlogic.gdx.scenes.scene2d.utils.{ChangeListener => GdxChangeListener}
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent

object ChangeListener {
  def apply(f: => Unit): GdxChangeListener = new GdxChangeListener {
    def changed(event: ChangeEvent, actor: Actor) = f
  }

  def once(f: => Unit): GdxChangeListener = new GdxChangeListener {
    def changed(event: ChangeEvent, actor: Actor) = {
      event.getListenerActor.removeListener(this)
      f
    }
  }
}
