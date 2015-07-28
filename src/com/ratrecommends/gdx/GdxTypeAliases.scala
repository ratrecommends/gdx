package com.ratrecommends.gdx

trait GdxTypeAliases {
  type Skin = com.badlogic.gdx.scenes.scene2d.ui.Skin
  type I18NBundle = com.badlogic.gdx.utils.I18NBundle
  type Actor = com.badlogic.gdx.scenes.scene2d.Actor
  type Group = com.badlogic.gdx.scenes.scene2d.Group
  type Container[T <: Actor] = com.badlogic.gdx.scenes.scene2d.ui.Container[T]
  type Image = com.badlogic.gdx.scenes.scene2d.ui.Image
  type TextButton = com.badlogic.gdx.scenes.scene2d.ui.TextButton
  type WidgetGroup = com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
  type InputListener = com.badlogic.gdx.scenes.scene2d.InputListener
  type InputEvent = com.badlogic.gdx.scenes.scene2d.InputEvent
  type Rectangle = com.badlogic.gdx.math.Rectangle
  type Label = com.badlogic.gdx.scenes.scene2d.ui.Label
  type Table = com.badlogic.gdx.scenes.scene2d.ui.Table
  type Vector2 = com.badlogic.gdx.math.Vector2
  type HorizontalGroup = com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
  type ShapeRenderer = com.badlogic.gdx.graphics.glutils.ShapeRenderer
  type Batch = com.badlogic.gdx.graphics.g2d.Batch
  type Stage = com.badlogic.gdx.scenes.scene2d.Stage
  type Viewport = com.badlogic.gdx.utils.viewport.Viewport
  type ScalingViewport = com.badlogic.gdx.utils.viewport.ScalingViewport
  type OrthographicCamera = com.badlogic.gdx.graphics.OrthographicCamera
  type StageChecker = com.ratrecommends.gdx.scene2d.utils.StageChecker
  type Window[A] = com.ratrecommends.gdx.scene2d.Window[A]

  type ShapeActor = com.ratrecommends.gdx.scene2d.ShapeActor

}
