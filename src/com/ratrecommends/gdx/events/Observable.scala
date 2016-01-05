package com.ratrecommends.gdx.events

trait Observable[A] {

  def add(f: A => Unit): Unit

  def remove(f: A => Unit): Unit
}
