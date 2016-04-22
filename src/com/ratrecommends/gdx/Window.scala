package com.ratrecommends.gdx

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Interpolation._
import com.badlogic.gdx.scenes.scene2d.{Event, EventListener}
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.ratrecommends.gdx.Window.WindowStyle
import com.ratrecommends.gdx.WindowEventType._
import com.ratrecommends.gdx.util.StyleCompanion

final class Window[A, B](val style: WindowStyle, val view: Actor) {

  private var shown = false
  private var previousKeyboardFocus: Actor = _
  private var windowParams: WindowParams[A] = _

  private[gdx] val root: WidgetGroup = new WidgetGroup with StageChecker.Added {
    def addedToStage(stage: Stage) = {
      stage.cancelTouchFocus()
      previousKeyboardFocus = stage.getKeyboardFocus
      stage.setKeyboardFocus(root)
    }
  }.fillParent(true).withListener(InputListener(
    onTouchDown = _.stop(),
    onKeyDown = event => {
      event.stop()
      if (windowParams.canClose && (event.getKeyCode == Input.Keys.ESCAPE || event.getKeyCode == Input.Keys.BACK)) {
        hide(null.asInstanceOf[B])
      }
    }
  ))

  private val backContainer = new Image(style.background).wrap().fill().fillParent(true).addTo(root).onTap {
    if (windowParams.canClose) hide(null.asInstanceOf[B])
  }
  private val content = view.wrap().fillParent(true).alpha(0).position(0, -style.contentAnimationOffset).addTo(root)

  if (style.backgroundColor != null) {
    backContainer.getActor.setColor(style.backgroundColor)
  }
  backContainer.alpha(0)


  def show(at: Group)(implicit ev: Unit <:< A): this.type = show(at, WindowParams(()))

  def hide()(implicit ev: Unit <:< B): Unit = hide(())

  def show(at: Group, params: WindowParams[A]): this.type = {
    if (!shown) {
      shown = true
      windowParams = params
      at.addActor(root)
      root.clearActions()
      root.addAction(delay(0.3f, () => notify(Shown, params = params)))
      backContainer.clearActions()
      backContainer.addAction(fadeIn(0.3f, fade))
      content.setTouchable(Touchable.childrenOnly)
      content.clearActions()
      content.addAction(moveTo(0, 0, 0.3f, circleOut))
      content.addAction(fadeIn(0.3f, fade))
      notify(Show, params = params)
    }
    notify(Refresh, params = params)
    this
  }

  def hide(result: B): Unit = if (shown) {
    shown = false
    if (root.getStage != null) {
      root.getStage.setKeyboardFocus(previousKeyboardFocus)
      previousKeyboardFocus = null
    }
    content.setTouchable(Touchable.disabled)
    content.clearActions()
    content.addAction(moveTo(0, -style.contentAnimationOffset, 0.3f, circleIn))
    content.addAction(fadeOut(0.3f, fade))
    backContainer.clearActions()
    backContainer.addAction(fadeOut(0.3f, fade))
    root.clearActions()
    root.addAction(delay(0.3f, () => {
      notify(Hidden, result = result)
      root.remove()
    }))
    notify(Hide, result = result)
  }


  private[gdx] def notify(windowEventType: WindowEventType,
                          params: WindowParams[A] = null.asInstanceOf[A],
                          result: B = null.asInstanceOf[B]) = {
    val e = Pools.obtain[WindowEvent[A, B]]
    e.window = this
    e.eventType = windowEventType
    e.params = params
    e.result = result
    root.fire(e)
    Pools.free(e)
  }

  private def subscribe(pf: PartialFunction[WindowEventType, WindowEvent[A, B] => Unit]): this.type = {
    root.addListener(WindowListener(pf))
    this
  }

  def onShow(callback: WindowParams[A] => Unit) = subscribe {
    case Show => e => callback(e.params)
  }

  def onShown(callback: WindowParams[A] => Unit) = subscribe {
    case Shown => e => callback(e.params)
  }

  def onRefresh(callback: WindowParams[A] => Unit) = subscribe {
    case Refresh => e => callback(e.params)
  }

  def onHide(callback: B => Unit) = subscribe {
    case Hide => e => callback(e.result)
  }

  def onHidden(callback: B => Unit) = subscribe {
    case Hidden => e => callback(e.result)
  }

}

case class WindowParams[A](value: A, canClose: Boolean = true)

object WindowParams {
  implicit def fromValue[A](value: A): WindowParams[A] = WindowParams(value)

  implicit def toValue[A](params: WindowParams[A]): A = params.value
}

object Window {

  def apply[A, B](view: Actor)(implicit style: WindowStyle) = new Window[A, B](style, view)

  final class WindowStyle {
    var background: Drawable = _
    /** Optional */
    var backgroundColor: Color = _

    var contentAnimationOffset: Int = 30
  }

  object WindowStyle extends StyleCompanion[WindowStyle]

}

sealed abstract class WindowEventType

object WindowEventType {

  case object Show extends WindowEventType

  case object Refresh extends WindowEventType

  case object Shown extends WindowEventType

  case object Hide extends WindowEventType

  case object Hidden extends WindowEventType

}

class WindowEvent[A, B] extends Event {
  var window: Window[A, B] = _
  var eventType: WindowEventType = _
  var params: WindowParams[A] = _
  var result: B = _

  override def reset() = {
    super.reset()
    window = null
    eventType = null
    params = null
    result = null.asInstanceOf[B]
  }
}

case class WindowListener[A, B](pf: PartialFunction[WindowEventType, WindowEvent[A, B] => Unit]) extends EventListener {
  def handle(event: Event) = event match {
    case we: WindowEvent[A, B] =>
      pf.runWith(_.apply(we))(we.eventType)
      true
    case _ => false
  }
}