package com.ratrecommends.gdx.scene2d

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation._
import com.badlogic.gdx.scenes.scene2d.Touchable._
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.{Event, EventListener}
import com.ratrecommends.gdx._
import com.ratrecommends.gdx.scene2d.Window.WindowStyle

class Window[A, B](val style: WindowStyle) {

  def this(skin: Skin, styleName: String) = this(skin.get(styleName, classOf[WindowStyle]))

  def this(skin: Skin) = this(skin, "default")

  final val root: WidgetGroup = new WidgetGroup with StageChecker {

    def addedToStage(stage: Stage): Unit = {
      stage.cancelTouchFocus()
      previousKeyboardFocus = stage.getKeyboardFocus
      stage.setKeyboardFocus(root)
    }

    def removedFromStage(stage: Stage): Unit = ()
  }

  private val backContainer = new Image(style.background).wrap().fill()

  protected final val content = new Container[Actor]()


  private final var shown = false
  private final var initialized = false
  private final var windowParams = WindowParams.default
  private final var previousKeyboardFocus: Actor = _

  content.setFillParent(true)
  content.getColor.a = 0
  content.setY(-Window.ContentAnimationOffset)

  root.setFillParent(true)
  root.addActor(backContainer)
  root.addActor(content)
  root.addListener(new InputListener {

    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      event.stop()
      true
    }

    override def keyDown(event: InputEvent, keycode: Int): Boolean = {
      event.stop()
      if (windowParams.canClose && (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK)) {
        hide(null.asInstanceOf[B])
      }
      true
    }
  })

  if (style.backgroundColor != null) {
    backContainer.getActor.setColor(style.backgroundColor)
  }
  backContainer.getColor.a = 0
  backContainer.setFillParent(true)
  backContainer.onTap {
    if (windowParams.canClose) hide(null.asInstanceOf[B])
  }

  final def show(at: Group)(implicit ev: Unit <:< A): this.type = show(at, ())

  final def show(at: Stage)(implicit ev: Unit <:< A): this.type = show(at, ())

  final def show(at: Stage, params: A): this.type = show(at.getRoot, params, WindowParams.default)

  final def show(at: Stage, params: A, windowParams: WindowParams): this.type = show(at.getRoot, params, windowParams)

  final def show(at: Group, params: A, windowParams: WindowParams = WindowParams.default): this.type = if (!shown) {
    shown = true
    this.windowParams = windowParams
    at.addActor(root)
    root.clearActions()
    root.addAction(delay(0.3f, () => notify(WindowEventType.Shown, params = params)))
    backContainer.clearActions()
    backContainer.addAction(fadeIn(0.3f, fade))
    content.setTouchable(childrenOnly)
    content.clearActions()
    content.addAction(moveTo(0, 0, 0.3f, circleOut))
    content.addAction(fadeIn(0.3f, fade))
    if (!initialized) {
      initialized = true
      onInit(params)
    }
    onShow(params)
    onRefresh(params)
    notify(WindowEventType.Show, params = params)
    this
  } else {
    onRefresh(params)
    this
  }

  final def hide()(implicit ev: Unit <:< B): Unit = hide(())

  final def hide(result: B) = if (shown) {
    shown = false
    if (root.getStage != null) {
      root.getStage.setKeyboardFocus(previousKeyboardFocus)
      previousKeyboardFocus = null
    }
    content.setTouchable(disabled)
    content.clearActions()
    content.addAction(moveTo(0, -Window.ContentAnimationOffset, 0.3f, circleIn))
    content.addAction(fadeOut(0.3f, fade))
    backContainer.clearActions()
    backContainer.addAction(fadeOut(0.3f, fade))
    root.clearActions()
    root.addAction(delay(0.3f, () => {
      notify(WindowEventType.Hidden, result = result)
      root.remove()
    }))
    onHide(result)
    notify(WindowEventType.Hide, result = result)
  }

  private final def notify(windowEventType: WindowEventType,
                           params: A = null.asInstanceOf[A],
                           result: B = null.asInstanceOf[B]) = {
    val e = Pools.obtain[WindowEvent[A, B]]
    e.window = this
    e.eventType = windowEventType
    e.params = params
    e.result = result
    root.fire(e)
  }

  final def isShown = shown

  final def subscribe(listener: WindowListener[A, B]): this.type = {
    root.addListener(listener)
    this
  }

  final def onShow(code: => Unit): this.type = subscribe(WindowListener.show(_ => code))

  final def onShow(f: A => Unit): this.type = subscribe(WindowListener.show(f))

  final def onShown(code: => Unit): this.type = subscribe(WindowListener.shown(_ => code))

  final def onShown(f: A => Unit): this.type = subscribe(WindowListener.shown(f))

  final def onHide(code: => Unit): this.type = subscribe(WindowListener.hide(_ => code))

  final def onHide(f: B => Unit): this.type = subscribe(WindowListener.hide(f))

  final def onHidden(code: => Unit): this.type = subscribe(WindowListener.hidden(_ => code))

  final def onHidden(f: B => Unit): this.type = subscribe(WindowListener.hidden(f))

  protected def onInit(initialParams: A): Unit = ()

  protected def onShow(params: A): Unit = ()

  protected def onRefresh(params: A): Unit = ()

  protected def onHide(result: B): Unit = ()

}

object Window {

  val ContentAnimationOffset = 30

  final class WindowStyle {
    var background: Drawable = _
    /** Optional */
    var backgroundColor: Color = _
  }

}

class WindowListener[A, B] extends EventListener {
  override final def handle(event: Event): Boolean = event match {
    case we: WindowEvent[A, B] =>
      we.eventType match {
        case WindowEventType.Show => show(we)
        case WindowEventType.Shown => shown(we)
        case WindowEventType.Hide => hide(we)
        case WindowEventType.Hidden => hidden(we)
      }
      true
    case _ =>
      false
  }

  def show(windowEvent: WindowEvent[A, B]): Unit = ()

  def shown(windowEvent: WindowEvent[A, B]): Unit = ()

  def hide(windowEvent: WindowEvent[A, B]): Unit = ()

  def hidden(windowEvent: WindowEvent[A, B]): Unit = ()
}

object WindowListener {
  def show[A, B](f: A => Unit): WindowListener[A, B] = new WindowListener[A, B] {
    override def show(e: WindowEvent[A, B]) = f(e.params)
  }

  def shown[A, B](f: A => Unit): WindowListener[A, B] = new WindowListener[A, B] {
    override def shown(e: WindowEvent[A, B]) = f(e.params)
  }

  def hide[A, B](f: B => Unit): WindowListener[A, B] = new WindowListener[A, B] {
    override def hide(e: WindowEvent[A, B]) = if (e.result != null) f(e.result)
  }

  def hidden[A, B](f: B => Unit): WindowListener[A, B] = new WindowListener[A, B] {
    override def hidden(e: WindowEvent[A, B]) = if (e.result != null) f(e.result)
  }
}

class WindowEvent[A, B] extends Event {
  var window: Window[A, B] = _
  var eventType: WindowEventType = _
  var result: B = _
  var params: A = _

  override def reset() = {
    super.reset()
    window = null
    eventType = null
    params = null.asInstanceOf[A]
    result = null.asInstanceOf[B]
  }
}

sealed abstract class WindowEventType

object WindowEventType {

  case object Show extends WindowEventType

  case object Shown extends WindowEventType

  case object Hide extends WindowEventType

  case object Hidden extends WindowEventType

}


case class WindowParams(canClose: Boolean = true)

object WindowParams {
  val default = WindowParams()
}