package com.ratrecommends

import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.{AssetDescriptor, AssetLoaderParameters}
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener
import com.badlogic.gdx.scenes.scene2d.{Action, EventListener, Touchable}
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils._
import com.badlogic.gdx.utils.{ObjectMap, ObjectSet, SnapshotArray}

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

    def withListener(listener: EventListener): A = {
      actor.addListener(listener)
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

    def scrollable(): ScrollPane = new ScrollPane(actor)

    def color(value: Color): A = {
      actor.setColor(value)
      actor
    }

    def alpha(value: Float): A = {
      actor.getColor.a = value
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

    def onAct(f: => Unit): A = {
      actor.addAction(new Action {
        def act(delta: Float) = {
          f
          false
        }
      })
      actor
    }

    def onActUntilDone(f: => Boolean): A = {
      actor.addAction(new Action {
        var done = false

        def act(delta: Float) = {
          if (!done) {
            done = f
          }
          done
        }

        override def restart() = done = false
      })
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

    def toHorizontalGroup: HorizontalGroup = {
      val res = HorizontalGroup()
      seq.foreach(res.addActor)
      res
    }
  }

  implicit class RichStage[A <: Stage](val stage: A) extends AnyVal {
    def onKey(key: Int,
              ctrl: BooleanPredicate = BooleanPredicate.AnyMatches,
              alt: BooleanPredicate = BooleanPredicate.AnyMatches,
              shift: BooleanPredicate = BooleanPredicate.AnyMatches)(code: => Unit): InputListener = {
      val listener = InputListener(onKeyDown = e => {
        if (key == e.getKeyCode && ctrl.check(UIUtils.ctrl()) && shift.check(UIUtils.shift()) && alt.check(UIUtils.alt())) {
          code
        }
      })
      stage.addListener(listener)
      listener
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

    def enabled(value: Boolean): A = {
      disableable.setDisabled(!value)
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

  implicit class RichSkin(val s: Skin) extends AnyVal {

    def apply[A: ClassTag]: A = s.get(implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])

    def apply[A: ClassTag](name: String): A = s.get(name, implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])

  }

  implicit class RichObjectMap[K, V](val m: ObjectMap[K, V]) extends AnyVal {
    def getOrElseUpdate(key: K, fallback: => V): V = {
      if (m.containsKey(key)) {
        m.get(key)
      } else {
        val value = fallback
        m.put(key, value)
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
    def addIfNotContains(value: A, identity: Boolean): Boolean = {
      if (!arr.contains(value, identity)) {
        arr.add(value)
        true
      } else {
        false
      }
    }

    def foreach[R](f: A => R) = {
      var i = 0
      while (i < arr.size) {
        f(arr.get(i))
        i += 1
      }
    }

    def forall(p: A => Boolean) = {
      var res = true
      var i = 0
      while (res && i < arr.size) {
        res = p(arr.get(i))
        i += 1
      }
      res
    }

    def exists(p: A => Boolean) = {
      var res = false
      var i = 0
      while (!res && i < arr.size) {
        res = p(arr.get(i))
        i += 1
      }
      res
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

    def toSeq: IndexedSeq[A] = {
      val b = Vector.newBuilder[A]
      b.sizeHint(arr.size)
      arr.foreach(b +=)
      b.result()
    }
  }

  implicit class RichTextField[A <: TextField](val textField: A) extends AnyVal {
    def messageText(value: String): A = {
      textField.setMessageText(value)
      textField
    }

    def onSubmit(f: String => Unit): A = {
      textField.addListener(new InputListener {
        override def keyDown(event: InputEvent, keycode: Int) = {
          if (keycode == Input.Keys.ENTER) {
            f(textField.getText)
            true
          } else {
            super.keyDown(event, keycode)
          }
        }
      })
      textField
    }

    def selected(): A = {
      textField.selectAll()
      textField
    }

    def focusedOn(stage: Stage): A = {
      stage.setKeyboardFocus(textField)
      textField
    }
  }

  implicit class RichImage[A <: Image](val image: A) extends AnyVal {
    def scaling(scaling: Scaling): A = {
      image.setScaling(scaling)
      image
    }
  }

  implicit class RichI18nBundle(val bundle: I18NBundle) extends AnyVal {
    def apply(key: String) = bundle.get(key)

    def apply(key: String, args: Any*) = bundle.format(key, args.map(String.valueOf): _*)
  }

  implicit def stage2Group(stage: Stage): Group = stage.getRoot

}
