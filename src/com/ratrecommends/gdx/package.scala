package com.ratrecommends

import com.badlogic.gdx.assets.{AssetDescriptor, AssetLoaderParameters}
import com.badlogic.gdx.scenes.scene2d.{Touchable, Action}
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils._
import com.badlogic.gdx.utils.{ObjectSet, SnapshotArray, ObjectMap}

import scala.reflect.ClassTag

package object gdx extends GdxTypeAliases with GdxExecutionContext with GdxNet {

  implicit class RichActor[A <: Actor](val actor: A) extends AnyVal {

    def parentStream: Stream[Actor] = actor.getParent match {
      case null => Stream.empty
      case v => v #:: v.parentStream
    }

    def ascendantStream: Stream[Actor] = actor #:: parentStream

    def onChangeAndNow(code: => Unit): A = {
      code
      onChange(code)
    }

    def onChangeAndNow(f: A => Unit): A = {
      f(actor)
      onChange(f)
    }

    def onChange(code: => Unit): A = {
      actor.addListener(ChangeListener(code))
      actor
    }

    def onChange(f: A => Unit): A = {
      actor.addListener(ChangeListener(f(actor)))
      actor
    }

    def onNextChange(code: => Unit): A = {
      actor.addListener(ChangeListener.once(code))
      actor
    }

    def onNextChange(f: A => Unit): A = {
      actor.addListener(ChangeListener.once(f(actor)))
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

    def position(x: Float, y: Float): A = {
      actor.setPosition(x, y)
      actor
    }

    def position(x: Float, y: Float, align: Int): A = {
      actor.setPosition(x, y, align)
      actor
    }

    def position(vec: Vector2): A = {
      actor.setPosition(vec.x, vec.y)
      actor
    }

    def move(dx: Float, dy: Float): A = {
      actor.moveBy(dx, dy)
      actor
    }

    def touchable(value: Touchable): A = {
      actor.setTouchable(value)
      actor
    }

    def widthTo(w: Float): A = {
      actor.setWidth(w)
      actor
    }

    def heightTo(h: Float): A = {
      actor.setHeight(h)
      actor
    }

    def sizeTo(w: Float, h: Float): A = {
      actor.setSize(w, h)
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

  implicit class RichStage[A <: Stage](val stage: A) extends AnyVal {
    def onKey(key: Int,
              ctrl: BooleanPredicate = BooleanPredicate.AnyMatches,
              alt: BooleanPredicate = BooleanPredicate.AnyMatches,
              shift: BooleanPredicate = BooleanPredicate.AnyMatches)(code: => Unit): A = {
      stage.addListener(new InputListener {
        override def keyUp(event: InputEvent, keycode: Int) = {

          if (key == keycode && ctrl.check(UIUtils.ctrl()) && shift.check(UIUtils.shift()) && alt.check(UIUtils.alt())) {
            code
            true
          } else {
            super.keyUp(event, keycode)
          }
        }
      })
      stage
    }
  }

  implicit class RichLabel[A <: Label](val label: A) extends AnyVal {
    def textWrap(value: Boolean): A = {
      label.setWrap(value)
      label
    }

    def fontScale(value: Float): A = {
      label.setFontScale(value)
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

    def addAll(actors: Actor*): A = {
      actors.foreach(_.addTo(group))
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

    override def restart() = ran = false
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

  implicit class RichObjectSet[A](val set: ObjectSet[A]) extends AnyVal {
    def foreach(f: A => Unit): Unit = {
      val it = set.iterator()
      while (it.hasNext()) {
        f(it.next())
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

  implicit class RichArray[A](val arr: com.badlogic.gdx.utils.Array[A]) extends AnyVal {
    def addIfNotContains(value: A, identity: Boolean) = {
      if (!arr.contains(value, identity)) arr.add(value)
    }

    def foreach[R](f: A => R) = {
      var i = 0
      while (i < arr.size) {
        f(arr.get(i))
        i += 1
      }
    }

    def minBy[B: Ordering](f: A => B) = {
      var minF: B = null.asInstanceOf[B]
      var minElem: A = null.asInstanceOf[A]
      var first = true
      val cmp = implicitly[Ordering[B]]
      var i = 0
      while (i < arr.size) {
        val elem = arr.get(i)
        val fx = f(elem)
        if (first || cmp.lt(fx, minF)) {
          minElem = elem
          minF = fx
          first = false
        }
        i += 1
      }
      minElem
    }
  }

  implicit class RichTextField[A <: TextField](val textField: A) extends AnyVal {
    def messageText(value: String): A = {
      textField.setMessageText(value)
      textField
    }
  }

}
