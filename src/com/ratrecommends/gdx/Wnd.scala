package com.ratrecommends.gdx

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Interpolation._
import com.badlogic.gdx.scenes.scene2d.{Event, EventListener}
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.ratrecommends.gdx.Wnd.WindowStyle
import com.ratrecommends.gdx.WndEventType._

final class Wnd[A, B](val style: StyleHolder, val view: Actor) extends WndEvents[A, B] {

  private var shown = false
  private var previousKeyboardFocus: Actor = _
  private var windowParams: WndParams[A] = _

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


  def show(at: Target)(implicit ev: Unit <:< A): this.type = show(at, WndParams(()))

  def hide()(implicit ev: Unit <:< B): Unit = hide(())

  def show(at: Target, params: WndParams[A]): this.type = {
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

}

case class WndParams[A](value: A, canClose: Boolean = true)

object WndParams {
  implicit def fromValue[A](value: A): WndParams[A] = WndParams(value)

  implicit def toValue[A](params: WndParams[A]): A = params.value
}

object Wnd {

  def apply[A, B](view: Actor)(implicit style: StyleHolder) = new Wnd[A, B](style, view)

  final class WindowStyle {
    var background: Drawable = _
    /** Optional */
    var backgroundColor: Color = _

    var contentAnimationOffset: Int = 30
  }

}

case class Target(group: Group)

object Target {

  implicit def fromGroup(group: Group): Target = Target(group)

  implicit def fromStage(stage: Stage): Target = Target(stage.getRoot)

  implicit def toGroup(target: Target): Group = target.group
}

case class StyleHolder(style: WindowStyle)

object StyleHolder {
  implicit def fromStyle(style: WindowStyle): StyleHolder = StyleHolder(style)

  implicit def fromSkin(skin: Skin): StyleHolder = StyleHolder(skin.apply[WindowStyle])

  implicit def fromStyleNameSkin(name: String)(implicit skin: Skin): StyleHolder = StyleHolder(skin.apply[WindowStyle](name))

  implicit def fromImplicitSkin(implicit skin: Skin): StyleHolder = StyleHolder(skin.apply[WindowStyle])

  implicit def toStyle(holder: StyleHolder): WindowStyle = holder.style
}

sealed abstract class WndEventType

object WndEventType {

  case object Show extends WndEventType

  case object Refresh extends WndEventType

  case object Shown extends WndEventType

  case object Hide extends WndEventType

  case object Hidden extends WndEventType

}

class WndEvent[A, B] extends Event {
  var window: Wnd[A, B] = _
  var eventType: WndEventType = _
  var params: WndParams[A] = _
  var result: B = _

  override def reset() = {
    super.reset()
    window = null
    eventType = null
    params = null
    result = null.asInstanceOf[B]
  }
}

case class WndListener[A, B](pf: PartialFunction[WndEventType, WndEvent[A, B] => Unit]) extends EventListener {
  def handle(event: Event) = event match {
    case we: WndEvent[A, B] =>
      pf.runWith(_.apply(we))(we.eventType)
      true
    case _ => false
  }
}

trait WndEvents[A, B] {
  this: Wnd[A, B] =>

  private[gdx] def notify(windowEventType: WndEventType,
                          params: WndParams[A] = null.asInstanceOf[A],
                          result: B = null.asInstanceOf[B]) = {
    val e = Pools.obtain[WndEvent[A, B]]
    e.window = this
    e.eventType = windowEventType
    e.params = params
    e.result = result
    root.fire(e)
    Pools.free(e)
  }

  private final def subscribe(pf: PartialFunction[WndEventType, WndEvent[A, B] => Unit]): this.type = {
    root.addListener(WndListener(pf))
    this
  }

  def onShow(callback: WndParams[A] => Unit) = subscribe {
    case Show => e => callback(e.params)
  }

  def onShown(callback: WndParams[A] => Unit) = subscribe {
    case Shown => e => callback(e.params)
  }

  def onRefresh(callback: WndParams[A] => Unit) = subscribe {
    case Refresh => e => callback(e.params)
  }

  def onHide(callback: B => Unit) = subscribe {
    case Hide => e => callback(e.result)
  }

  def onHidden(callback: B => Unit) = subscribe {
    case Hidden => e => callback(e.result)
  }
}