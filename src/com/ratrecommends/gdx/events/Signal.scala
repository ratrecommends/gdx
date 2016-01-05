package com.ratrecommends.gdx.events

import com.badlogic.gdx.utils.SnapshotArray
import com.ratrecommends.gdx._

final class Signal[A <: AnyRef] extends Dispatcher[A] with Observable[A] {

  private val listeners = new SnapshotArray[A => Unit]()

  def add(f: A => Unit): Unit = listeners.addIfNotContains(f, identity = true)

  def remove(f: A => Unit): Unit = listeners.removeValue(f, true)

  def dispatch(a: A): Unit = listeners.foreach(_.apply(a))
}
