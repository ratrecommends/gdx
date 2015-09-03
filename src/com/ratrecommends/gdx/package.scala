package com.ratrecommends

import com.badlogic.gdx.assets.{AssetDescriptor, AssetLoaderParameters}
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils._
import com.badlogic.gdx.utils.{SnapshotArray, ObjectMap}

import scala.reflect.ClassTag

package object gdx extends GdxTypeAliases with GdxExecutionContext with GdxNet {

  implicit class RichActor[A <: Actor](val actor: A) extends AnyVal {

    def parentStream: Stream[Actor] = actor.getParent match {
      case null => Stream.empty
      case v => v #:: v.parentStream
    }

    def ascendantStream: Stream[Actor] = actor #:: parentStream

    def onChange(code: => Unit): A = {
      actor.addListener(new ChangeListener {
        override def changed(event: ChangeEvent, actor: Actor): Unit = code
      })
      actor
    }

    def onNextChange(code: => Unit): A = {
      actor.addListener(new ChangeListener {
        override def changed(event: ChangeEvent, actor: Actor): Unit = {
          actor.removeListener(this)
          code
        }
      })
      actor
    }

    def onTap(code: => Unit): A = {
      actor.addListener(new ActorGestureListener() {
        override def tap(event: InputEvent, x: Float, y: Float, count: Int, button: Int): Unit = code
      })
      actor
    }

    def visible(value: Boolean): A = {
      actor.setVisible(value)
      actor
    }

    def addTo(group: Group): A = {
      group.addActor(actor)
      actor
    }

    def addTo(stage: Stage): A = addTo(stage.getRoot)

    def wrap(): Container[A] = new Container(actor)

    def color(value: Color): A = {
      actor.setColor(value)
      actor
    }

  }

  implicit class RichContainer[A <: Container[_]](val container: A) extends AnyVal {
    def background(drawable: Drawable): A = {
      container.setBackground(drawable)
      container
    }
  }

  implicit class RichIterable[A <: Actor](val seq: Iterable[A]) extends AnyVal {
    def toTable(defaults: Cell[_] => Unit = cell => (), vertical: Boolean = false): Table = {
      val t = new Table
      defaults(t.defaults())
      seq.foreach { actor =>
        val cell = t.add(actor)
        if (vertical) cell.row()
      }
      t
    }
  }

  implicit class RichLabel[A <: Label](val label: A) extends AnyVal {
    def textWrap(value: Boolean): A = {
      label.setWrap(value)
      label
    }

    def alignment(value: Int): A = {
      label.setAlignment(value)
      label
    }
  }

  implicit class RichSelectBox[A, B <: SelectBox[A]](val selectBox: B) extends AnyVal {
    def items(seq: A*): B = {
      selectBox.setItems(seq: _*)
      selectBox
    }

    def items(seq: Iterable[A]): B = {
      selectBox.setItems(seq.toSeq: _*)
      selectBox
    }
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

    def packed(): A = {
      widget.pack()
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

  implicit class RichDisableable[A <: Disableable](val disableable: A) extends AnyVal {
    def disabled(value: Boolean): A = {
      disableable.setDisabled(value)
      disableable
    }
  }

  implicit def func2action[A](f: () => A): Action = new Action {
    var ran = false

    override def act(delta: Float): Boolean = {
      if (!ran) {
        ran = true
        f()
      }
      true
    }
  }


  implicit class RichObjectMap[K, V](val map: ObjectMap[K, V]) extends AnyVal {
    def getOrElseUpdate(key: K, fallback: => V): V = {
      if (map.containsKey(key)) {
        map.get(key)
      } else {
        val value = fallback
        map.put(key, value)
        value
      }
    }
  }

  implicit class RichSnapshotArray[V](val arr: SnapshotArray[V]) extends AnyVal {
    def foreach[R](f: V => R) = {
      val items = arr.begin()
      val size = arr.size
      var i = 0
      while (i < size) {
        f(items(i))
        i += 1
      }
      arr.end()
    }
  }

  implicit class RichArray[V](val arr: com.badlogic.gdx.utils.Array[V]) extends AnyVal {
    def addIfNotContains(value: V, identity: Boolean) = {
      if (!arr.contains(value, identity)) arr.add(value)
    }
  }

  implicit class RichTextField[A <: TextField](val textField: A) extends AnyVal {
    def messageText(value: String): A = {
      textField.setMessageText(value)
      textField
    }
  }

}
