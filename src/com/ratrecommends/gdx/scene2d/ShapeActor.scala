package com.ratrecommends.gdx.scene2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.ratrecommends.gdx._

abstract class ShapeActor(final val renderer: ShapeRenderer) extends Actor {

  final var lineWidth = 1
  final var blendingEnabled = true

  final override def draw(batch: Batch, parentAlpha: Float): Unit = {
    batch.end()
    val color = getColor
    renderer.setColor(color.r, color.g, color.b, color.a * parentAlpha)
    renderer.setProjectionMatrix(batch.getProjectionMatrix)
    renderer.setTransformMatrix(batch.getTransformMatrix)
    if (blendingEnabled) Gdx.gl20.glEnable(GL20.GL_BLEND)
    Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    Gdx.gl20.glLineWidth(lineWidth)
    draw(renderer, parentAlpha)
    if (blendingEnabled) Gdx.gl20.glDisable(GL20.GL_BLEND)
    batch.begin()
  }

  protected def draw(renderer: ShapeRenderer, parentAlpha: Float)
}
