package com.ratrecommends.gdx

import com.badlogic.gdx.Gdx

import scala.concurrent.ExecutionContext

trait GdxExecutionContext {
  implicit val executionContext: ExecutionContext = new ExecutionContext {
    override def execute(runnable: Runnable): Unit = Gdx.app.postRunnable(runnable)

    override def reportFailure(cause: Throwable): Unit = Gdx.app.error("com.ratrecommends.gdx", "execution failed", cause)
  }
}
