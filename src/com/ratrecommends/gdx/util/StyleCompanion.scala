package com.ratrecommends.gdx.util

import com.ratrecommends.gdx._

import scala.reflect.ClassTag

class StyleCompanion[A: ClassTag] {

  implicit def fromSkin(skin: Skin): A = skin.apply[A]

  implicit def fromStyleNameSkin(name: String)(implicit skin: Skin): A = skin.apply[A](name)

  implicit def fromImplicitSkin(implicit skin: Skin): A = skin.apply[A]
}
