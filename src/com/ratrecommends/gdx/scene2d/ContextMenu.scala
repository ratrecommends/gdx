package com.ratrecommends.gdx.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.ratrecommends.gdx._
import com.ratrecommends.gdx.scene2d.ContextMenu.ContextMenuStyle

class ContextMenu[A](style: ContextMenuStyle) {

  private val group = VerticalGroup().fill()
  private val root = group.wrap().background(style.background)
  private var child: Option[ContextMenu[A]] = None
  private var parent: Option[ContextMenu[A]] = None
  private var callback: A => Unit = _

  def show(at: Stage, callback: A => Unit): Unit = show(at.getRoot, callback)

  def show(at: Group, callback: A => Unit): Unit = {
    show(at, callback, at.stageToLocalCoordinates {
      at.getStage.screenToStageCoordinates {
        Vector2().set(Gdx.input.getX, Gdx.graphics.getHeight - Gdx.input.getY)
      }
    })
  }

  private def show(at: Group, callback: A => Unit, vec: Vector2): Unit = {
    root.addTo(at).position(vec)
    this.callback = callback
  }

  private def hide(hideParent: Boolean = true): Unit = {
    root.remove()
    if (hideParent) {
      parent.foreach { menu =>
        parent = None
        menu.hide()
      }
    } else {
      parent = None
    }
  }

  def add(text: String)(f: () => A): Unit = add(LeafItem(text, f))

  def add(item: ContextMenuItem[A]): Unit = {
    val view = Label(item.text, style.label).wrap().fill().packed().addTo(group)
    item match {
      case leaf: LeafItem[A] =>
        view.addListener(InputListener(
          onEnter = e => view.background(style.background),
          onExit = e => view.background(null),
          onTouchDown = e => {
            callback(leaf.f())
            hide()
          }
        ))
      case branch: BranchItem[A] =>
        view.addListener(InputListener(
          onEnter = e => {
            view.background(style.highlight)
            child.foreach { menu =>
              child = None
              menu.hide(hideParent = false)
            }
            val m = new ContextMenu[A](style)
            m.parent = Some(this)
            branch.items.foreach(m.add)
            child = Some(m)
            m.show(root.getStage.getRoot, callback, view.localToStageCoordinates(Vector2(view.getWidth, view.getHeight)))
          },
          onExit = e => {
            view.background(null)
          }
        ))
    }
  }

}

object ContextMenu {

  class ContextMenuStyle {
    var label: LabelStyle = _
    var background: Drawable = _
    var highlight: Drawable = _
  }

}

sealed trait ContextMenuItem[A] {
  def text: String

  override def toString = text
}

case class LeafItem[A](text: String, f: () => A) extends ContextMenuItem[A]

case class BranchItem[A](text: String, items: ContextMenuItem[A]*) extends ContextMenuItem[A]