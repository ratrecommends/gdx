package com.ratrecommends.gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

class StageState(viewport: Viewport = null, batch: Batch = null) extends AppState {

  final val stage: Stage = {
    if (viewport != null && batch != null) {
      new Stage(viewport, batch)
    } else if (viewport != null && batch == null) {
      new Stage(viewport)
    } else if (viewport == null && batch != null) {
      new Stage(
        new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth, Gdx.graphics.getHeight, new OrthographicCamera()),
        batch
      )
    } else {
      new Stage()
    }
  }

  val backgroundColor = new Color(Color.DarkGray)

  override protected final def entered(): Unit = {
    onEntered()
  }

  override protected final def resumed(): Unit = {
    Gdx.input.setInputProcessor(stage)
    onResumed()
  }

  override protected final def resized(width: Int, height: Int): Unit = {
    stage.getViewport.update(width, height, true)
    onResized(width, height)
  }

  override protected final def rendered(): Unit = {
    onPrerendered()
    Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    stage.act()
    stage.draw()
    onRendered()
  }

  override protected final def paused(): Unit = {
    Gdx.input.setInputProcessor(null)
    onPaused()
  }

  override protected final def exited(): Unit = {
    stage.dispose()
    onExited()
  }

  protected def onEntered(): Unit = ()

  protected def onResumed(): Unit = ()

  protected def onResized(width: Int, height: Int): Unit = ()

  protected def onPrerendered(): Unit = ()

  protected def onRendered(): Unit = ()

  protected def onPaused(): Unit = ()

  protected def onExited(): Unit = ()
}
