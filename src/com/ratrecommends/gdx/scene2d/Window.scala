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

class Window[A](val style: WindowStyle) {

  def this(skin: Skin, styleName: String) = this(skin.get(styleName, classOf[WindowStyle]))

  def this(skin: Skin) = this(skin, "default")

  val root: WidgetGroup = new WidgetGroup with StageChecker {

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
        hide()
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
    if (windowParams.canClose) hide()
  }

  final def show(at: Stage, params: A): this.type = show(at.getRoot, params, WindowParams.default)

  final def show(at: Stage, params: A, windowParams: WindowParams): this.type = show(at.getRoot, params, windowParams)

  final def show(at: Group, params: A, windowParams: WindowParams = WindowParams.default): this.type = if (!shown) {
    shown = true
    this.windowParams = windowParams
    at.addActor(root)
    root.clearActions()
    root.addAction(delay(0.3f, () => notify(WindowEventType.Shown)))
    backContainer.clearActions()
    backContainer.addAction(fadeIn(0.3f, fade))
    content.setTouchable(childrenOnly)
    content.clearActions()
    content.addAction(moveTo(0, 0, 0.3f, circleOut))
    content.addAction(fadeIn(0.3f, fade))
    if (!initialized) {
      initialized = true
      onInit()
    }
    onShow(params)
    onRefresh(params)
    notify(WindowEventType.Show)
    this
  } else {
    onRefresh(params)
    this
  }

  final def hide() = if (shown) {
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
    root.addAction(delay(0.3f, sequence(removeActor(), () => notify(WindowEventType.Hidden))))
    onHide()
    notify(WindowEventType.Hide)
  }

  private final def notify(windowEventType: WindowEventType) = {
    val e = Pools.obtain[WindowEvent]
    e.window = this
    e.eventType = windowEventType
    root.fire(e)
  }

  final def isShown = shown

  final def onShow[U](code: => U): this.type = {
    root.addListener(new WindowListener {
      override def show(windowEvent: WindowEvent): Unit = code
    })
    this
  }

  final def onShown[U](code: => U): this.type = {
    root.addListener(new WindowListener {
      override def shown(windowEvent: WindowEvent): Unit = code
    })
    this
  }

  final def onHide[U](code: => U): this.type = {
    root.addListener(new WindowListener {
      override def hide(windowEvent: WindowEvent): Unit = code
    })
    this
  }

  final def onHidden[U](code: => U): this.type = {
    root.addListener(new WindowListener {
      override def hidden(windowEvent: WindowEvent): Unit = code
    })
    this
  }

  protected def onInit(): Unit = ()

  protected def onShow(params: A): Unit = ()

  protected def onRefresh(params: A): Unit = ()

  protected def onHide(): Unit = ()

}

object Window {

  val ContentAnimationOffset = 30

  final class WindowStyle {
    var background: Drawable = _
    /** Optional */
    var backgroundColor: Color = _
  }

}

class WindowListener extends EventListener {
  override final def handle(event: Event): Boolean = event match {
    case we: WindowEvent =>
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

  def show(windowEvent: WindowEvent): Unit = ()

  def shown(windowEvent: WindowEvent): Unit = ()

  def hide(windowEvent: WindowEvent): Unit = ()

  def hidden(windowEvent: WindowEvent): Unit = ()
}

class WindowEvent extends Event {
  var window: Window[_] = _
  var eventType: WindowEventType = _
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