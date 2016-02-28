package com.ratrecommends.gdx

import com.badlogic.gdx.scenes.scene2d.{InputListener => GdxInputListener, Event}

object InputListener {

  private final val empty: InputEvent => Unit = _ => ()

  def apply(onKeyTyped: InputEvent => Unit = empty,
            onMouseMoved: InputEvent => Unit = empty,
            onKeyDown: InputEvent => Unit = empty,
            onTouchDown: InputEvent => Unit = empty,
            onKeyUp: InputEvent => Unit = empty,
            onEnter: InputEvent => Unit = empty,
            onScrolled: InputEvent => Unit = empty,
            onTouchUp: InputEvent => Unit = empty,
            onExit: InputEvent => Unit = empty,
            onTouchDragged: InputEvent => Unit = empty
           ): GdxInputListener = new GdxInputListener {


    def handle(f: InputEvent => Unit, event: InputEvent): Boolean = if (f != empty) {
      f(event)
      true
    } else {
      false
    }

    override def keyTyped(event: InputEvent, character: Char) = handle(onKeyTyped, event)

    override def mouseMoved(event: InputEvent, x: Float, y: Float) = handle(onMouseMoved, event)

    override def keyDown(event: InputEvent, keycode: Int) = handle(onKeyDown, event)

    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) = handle(onTouchDown, event)

    override def keyUp(event: InputEvent, keycode: Int) = handle(onKeyUp, event)

    override def enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor) = handle(onEnter, event)

    override def scrolled(event: InputEvent, x: Float, y: Float, amount: Int) = handle(onScrolled, event)

    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) = handle(onTouchUp, event)

    override def exit(event: InputEvent, x: Float, y: Float, pointer: Int, toActor: Actor) = handle(onExit, event)

    override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) = handle(onTouchDown, event)
  }
}
