package com.ratrecommends.gdx.events

trait Dispatcher[A] {
  def dispatch(a: A):Unit
}
