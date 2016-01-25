package com.ratrecommends.gdx

sealed trait BooleanPredicate {
  def check(value: Boolean): Boolean
}

object BooleanPredicate {

  case object On extends BooleanPredicate {
    def check(value: Boolean) = value
  }

  case object Off extends BooleanPredicate {
    def check(value: Boolean) = !value
  }

  case object AnyMatches extends BooleanPredicate {
    def check(value: Boolean) = true
  }

  case object NothingMatches extends BooleanPredicate {
    def check(value: Boolean) = false
  }


}


