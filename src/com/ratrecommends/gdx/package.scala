package com.ratrecommends

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils.{Layout, ChangeListener, DragListener}
import com.badlogic.gdx.scenes.scene2d.{Actor, Group, InputEvent}

import scala.concurrent.ExecutionContext

package object gdx {

  implicit val executionContext: ExecutionContext = new ExecutionContext {
    override def execute(runnable: Runnable): Unit = Gdx.app.postRunnable(runnable)

    override def reportFailure(cause: Throwable): Unit = Gdx.app.log("com.ratrecommends.gdx", "execution failed", cause)
  }

  implicit class RichActor[A <: Actor](val actor: A) extends AnyVal {

    def parentStream: Stream[Actor] = actor.getParent match {
      case null => Stream.empty
      case v => v #:: v.parentStream
    }

    def ascendantStream: Stream[Actor] = actor #:: parentStream

    def onChange(code: => Unit): Unit = actor.addListener(new ChangeListener {
      override def changed(event: ChangeEvent, actor: Actor): Unit = code
    })

    def onDrag(f: (Float, Float) => Unit) = actor.addListener(new DragListener {
      override def drag(event: InputEvent, x: Float, y: Float, pointer: Int): Unit = f(getDeltaX, getDeltaY)
    })

    def visible(value: Boolean): A = {
      actor.setVisible(value)
      actor
    }

    def addTo(group: Group): A = {
      group.addActor(actor)
      actor
    }
  }

  implicit class RichLayout[A <: Layout](val widget: A) extends AnyVal {
    def fillParent(value: Boolean): A = {
      widget.setFillParent(value)
      widget
    }
  }

}
