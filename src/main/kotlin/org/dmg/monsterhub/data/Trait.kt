package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.service.Formula.toFormula
import java.math.BigDecimal
import javax.persistence.Entity

@Entity
class Trait : Feature() {
  var offenceBase: String? = null
  var offenceAlt: String? = null
  var defenceBase: String? = null
  var defenceAlt: String? = null
  var common: String? = null
  var commonAlt: String? = null

  fun formulas(context: (String) -> List<BigDecimal>) = sequenceOf(
      offenceBase,
      offenceAlt,
      defenceBase,
      defenceAlt,
      common,
      commonAlt
  )
      .map { it.toFormula(context) }

  companion object {
    val TRAIT = "TRAIT"
  }
}