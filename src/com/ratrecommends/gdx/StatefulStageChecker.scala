package com.ratrecommends.gdx


trait StatefulStageChecker[A] extends StageChecker {
  private[this] final var state: A = _

  final def addedToStage(stage: Stage) = state = onAddedToStage(stage)

  final def removedFromStage(stage: Stage) = {
    val local = state
    state = null.asInstanceOf[A]
    onRemovedFromStage(stage, local)
  }

  def onRemovedFromStage(stage: Stage, state: A): Unit

  def onAddedToStage(stage: Stage): A
}
