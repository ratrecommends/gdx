package com.ratrecommends.gdx

trait GdxTypeAliases {
  type Skin = com.badlogic.gdx.scenes.scene2d.ui.Skin
  type I18NBundle = com.badlogic.gdx.utils.I18NBundle
  type Actor = com.badlogic.gdx.scenes.scene2d.Actor
  type Group = com.badlogic.gdx.scenes.scene2d.Group
  type Container[T <: Actor] = com.badlogic.gdx.scenes.scene2d.ui.Container[T]
  type Image = com.badlogic.gdx.scenes.scene2d.ui.Image

  object Image {
    def apply(skin: Skin, drawableName: String): Image = new Image(skin, drawableName)
  }

  type TextButton = com.badlogic.gdx.scenes.scene2d.ui.TextButton

  object TextButton {
    def apply(text: String, skin: Skin): TextButton = new TextButton(text, skin)
  }

  type WidgetGroup = com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
  type InputListener = com.badlogic.gdx.scenes.scene2d.InputListener
  type InputEvent = com.badlogic.gdx.scenes.scene2d.InputEvent
  type Rectangle = com.badlogic.gdx.math.Rectangle
  type Label = com.badlogic.gdx.scenes.scene2d.ui.Label

  object Label {
    def apply(text: CharSequence, skin: Skin): Label = new Label(text, skin)
  }

  type Table = com.badlogic.gdx.scenes.scene2d.ui.Table
  type HorizontalGroup = com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup

  object HorizontalGroup {
    def apply(actors: Actor*): HorizontalGroup = {
      val g = new HorizontalGroup()
      actors.foreach(_.addTo(g))
      g
    }
  }

  type VerticalGroup = com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
  
  object VerticalGroup {
    def apply(actors:Actor*):VerticalGroup = {
      val g = new VerticalGroup()
      actors.foreach(_.addTo(g))
      g
    }
  }

  type Stage = com.badlogic.gdx.scenes.scene2d.Stage
  type ScrollPane = com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
  type Batch = com.badlogic.gdx.graphics.g2d.Batch
  type Vector2 = com.badlogic.gdx.math.Vector2
  type ShapeRenderer = com.badlogic.gdx.graphics.glutils.ShapeRenderer
  type Viewport = com.badlogic.gdx.utils.viewport.Viewport
  type ScalingViewport = com.badlogic.gdx.utils.viewport.ScalingViewport
  type OrthographicCamera = com.badlogic.gdx.graphics.OrthographicCamera
  type StageChecker = com.ratrecommends.gdx.scene2d.utils.StageChecker
  type Window[A] = com.ratrecommends.gdx.scene2d.Window[A]
  type ShapeActor = com.ratrecommends.gdx.scene2d.ShapeActor
  type Color = com.badlogic.gdx.graphics.Color

  object Color {
    val Clear = new Color(0, 0, 0, 0)
    val White = new Color(1, 1, 1, 1)
    val Black = new Color(0, 0, 0, 1)
    val Red = new Color(1, 0, 0, 1)
    val Green = new Color(0, 1, 0, 1)
    val Blue = new Color(0, 0, 1, 1)
    val LightGray = new Color(0.75f, 0.75f, 0.75f, 1)
    val Gray = new Color(0.5f, 0.5f, 0.5f, 1)
    val DarkGray = new Color(0.25f, 0.25f, 0.25f, 1)
    val Pink = new Color(1, 0.68f, 0.68f, 1)
    val Orange = new Color(1, 0.78f, 0, 1)
    val Yellow = new Color(1, 1, 0, 1)
    val Magenta = new Color(1, 0, 1, 1)
    val Cyan = new Color(0, 1, 1, 1)
    val Olive = new Color(0.5f, 0.5f, 0, 1)
    val Purple = new Color(0.5f, 0, 0.5f, 1)
    val Maroon = new Color(0.5f, 0, 0, 1)
    val Teal = new Color(0, 0.5f, 0.5f, 1)
    val Navy = new Color(0, 0, 0.5f, 1)

    def valueOf(hex: String): Color = com.badlogic.gdx.graphics.Color.valueOf(hex)
  }

  type Align = com.badlogic.gdx.utils.Align

  object Align {
    val center = 1 << 0
    val top = 1 << 1
    val bottom = 1 << 2
    val left = 1 << 3
    val right = 1 << 4
    val topLeft = top | left
    val topRight = top | right
    val bottomLeft = bottom | left
    val bottomRight = bottom | right
  }

}
