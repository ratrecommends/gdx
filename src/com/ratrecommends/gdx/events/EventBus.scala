package com.ratrecommends.gdx.events

import com.badlogic.gdx.utils.{SnapshotArray, ObjectMap}
import com.ratrecommends.gdx._

class EventBus[E[_]] {
  private val map = new ObjectMap[E[_], SnapshotArray[(E[_], Any) => Unit]]()

  def add[A](e: E[A])(f: (E[A], A) => Unit): (E[A], A) => Unit = {
    map.getOrElseUpdate(e, new SnapshotArray()).addIfNotContains(f.asInstanceOf[(E[_], Any) => Unit], identity = true)
    f
  }

  def remove[A](e: E[A])(f: (E[A], A) => Unit): (E[A], A) => Unit = {
    val arr = map.get(e)
    if (arr != null) {
      arr.removeValue(f.asInstanceOf[(E[_], Any) => Unit], true)
    }
    f
  }

  def dispatch[A](e: E[A], a: A): Unit = {
    val arr = map.get(e)
    if (arr != null) {
      arr.foreach(f => f(e, a))
    }
  }
}
