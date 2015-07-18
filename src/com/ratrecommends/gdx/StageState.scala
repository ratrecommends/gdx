package com.ratrecommends.gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{GL20, Color, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.{ScalingViewport, Viewport}

class StageState(viewport: Viewport = null, batch: Batch = null) extends AppState {

  private[this] final var _stage: Stage = _

  val backgroundColor = new Color(Color.DARK_GRAY)

  final def stage: Stage = _stage

  override protected final def entered(): Unit = {
    if (viewport != null && batch != null) {
      _stage = new Stage(viewport, batch)
    } else if (viewport != null && batch == null) {
      _stage = new Stage(viewport)
    } else if (viewport == null && batch != null) {
      _stage = new Stage(
        new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth, Gdx.graphics.getHeight, new OrthographicCamera()),
        batch
      )
    } else {
      _stage = new Stage()
    }
    onEntered()
  }

  override protected final def resumed(): Unit = {
    Gdx.input.setInputProcessor(stage)
    onResumed()
  }

  override protected final def resized(width: Int, height: Int): Unit = {
    _stage.getViewport.update(width, height, true)
    onResized(width, height)
  }

  override protected final def rendered(): Unit = {
    Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    _stage.act()
    _stage.draw()
    onRendered()
  }

  override protected final def paused(): Unit = {
    Gdx.input.setInputProcessor(null)
    onPaused()
  }

  override protected final def exited(): Unit = {
    _stage.dispose()
    onExited()
  }

  protected def onEntered(): Unit = ()

  protected def onResumed(): Unit = ()

  protected def onResized(width: Int, height: Int): Unit = ()

  protected def onRendered(): Unit = ()

  protected def onPaused(): Unit = ()

  protected def onExited(): Unit = ()
}
