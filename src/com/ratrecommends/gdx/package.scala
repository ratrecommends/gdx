package com.ratrecommends

import com.badlogic.gdx.assets.{AssetDescriptor, AssetLoaderParameters}
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils.{ChangeListener, Layout}

import scala.reflect.ClassTag

package object gdx extends GdxTypeAliases with GdxExecutionContext {

  implicit class RichActor[A <: Actor](val actor: A) extends AnyVal {

    def parentStream: Stream[Actor] = actor.getParent match {
      case null => Stream.empty
      case v => v #:: v.parentStream
    }

    def ascendantStream: Stream[Actor] = actor #:: parentStream

    def onChange(code: => Unit): Unit = actor.addListener(new ChangeListener {
      override def changed(event: ChangeEvent, actor: Actor): Unit = code
    })

    def visible(value: Boolean): A = {
      actor.setVisible(value)
      actor
    }

    def addTo(group: Group): A = {
      group.addActor(actor)
      actor
    }

    def wrap(): Container[A] = new Container(actor)

  }

  implicit class RichGroup[A <: Group](val group: A) extends AnyVal {

    def transform(value: Boolean): A = {
      group.setTransform(value)
      group
    }

    def children: Iterator[Actor] = {
      import collection.convert.wrapAsScala._
      group.getChildren.iterator()
    }

  }

  implicit class RichLayout[A <: Layout](val widget: A) extends AnyVal {

    def fillParent(value: Boolean): A = {
      widget.setFillParent(value)
      widget
    }

  }

  implicit class RichString(val str: String) extends AnyVal {

    def loadedAs[A: ClassTag] = {
      new AssetDescriptor[A](str, implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])
    }

    def loadedAs[A: ClassTag](params: AssetLoaderParameters[A] = null) = {
      new AssetDescriptor[A](str, implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]], params)
    }

  }

}
