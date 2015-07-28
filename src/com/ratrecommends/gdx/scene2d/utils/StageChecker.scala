package com.ratrecommends.gdx.scene2d.utils

import com.badlogic.gdx.scenes.scene2d.{Stage, Actor}

trait StageChecker extends Actor {
  abstract override def setStage(stage: Stage): Unit = {
    val prevStage = getStage
    super.setStage(stage)
    if (prevStage == null && stage != null) {
      addedToStage(stage)
    } else if (prevStage != null && stage == null) {
      removedFromStage(prevStage)
    }
  }

  def addedToStage(stage: Stage): Unit

  def removedFromStage(stage: Stage): Unit
}
