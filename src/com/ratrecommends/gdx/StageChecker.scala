package com.ratrecommends.gdx

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

object StageChecker {

  trait Added extends StageChecker {
    final def removedFromStage(stage: Stage) = ()
  }

  trait Removed extends StageChecker {
    final def addedToStage(stage: Stage) = ()
  }

}
