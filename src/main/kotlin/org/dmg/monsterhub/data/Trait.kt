package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.service.Formula
import java.math.BigDecimal
import javax.persistence.Entity

@Entity
class Trait : Feature() {
  var offenceBase: String? = null
  var offenceAlt: String? = null
  var defenceBase: String? = null
  var defenceAlt: String? = null
  var perceptionBase: String? = null
  var perceptionAlt: String? = null
  var handsBase: String? = null
  var handsAlt: String? = null
  var moveBase: String? = null
  var moveAlt: String? = null
  var common: String? = null

  fun formulas(context: (String) -> BigDecimal) = sequenceOf(
      offenceBase,
      offenceAlt,
      defenceBase,
      defenceAlt,
      perceptionBase,
      perceptionAlt,
      handsBase,
      handsAlt,
      moveBase,
      moveAlt,
      common
  )
      .map { Formula(it ?: "", context) }

  companion object {
    val TRAIT = "TRAIT"
  }
}