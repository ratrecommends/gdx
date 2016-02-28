package com.ratrecommends.gdx

import com.badlogic.gdx.utils

import scala.reflect.ClassTag

object  Pools {
  def obtain[A: ClassTag]: A = utils.Pools.obtain(implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])

  def free(value: AnyRef) = utils.Pools.free(value)
}
