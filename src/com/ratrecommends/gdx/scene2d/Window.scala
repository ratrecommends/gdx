package com.ratrecommends.gdx.scene2d

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation._
import com.badlogic.gdx.scenes.scene2d.Touchable._
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.ratrecommends.gdx._
import com.ratrecommends.gdx.scene2d.Window.WindowStyle

class Window[A](val style: WindowStyle) {

  def this(skin: Skin, styleName: String) = this(skin.get(styleName, classOf[WindowStyle]))

  def this(skin: Skin) = this(skin, "default")

  private val root: WidgetGroup = new WidgetGroup with StageChecker {

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

  final def show(at: Stage, params: A): Unit = show(at.getRoot, params, WindowParams.default)

  final def show(at: Stage, params: A, windowParams: WindowParams): Unit = show(at.getRoot, params, windowParams)

  final def show(at: Group, params: A, windowParams: WindowParams = WindowParams.default): Unit = if (!shown) {
    shown = true
    this.windowParams = windowParams
    at.addActor(root)
    root.clearActions()
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
  } else {
    onRefresh(params)
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
    root.addAction(delay(0.3f, removeActor()))
    onHide()
  }

  final def isShown = shown

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

case class WindowParams(canClose: Boolean = true)

object WindowParams {
  val default = WindowParams()
}