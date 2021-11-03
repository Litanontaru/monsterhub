package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.service.Formula.toFormula
import java.math.BigDecimal
import javax.persistence.Entity

@Entity
class Trait : Feature() {
  var base: String? = null
  var offenceAlt: String? = null
  var defenceAlt: String? = null
  var commonAlt: String? = null

  var overriding: Boolean = false

  fun formulas(context: (String) -> List<BigDecimal>) = sequenceOf(
      base,
      offenceAlt,
      defenceAlt,
      commonAlt
  )
      .map { it.toFormula(context) }

  companion object {
    val TRAIT = "TRAIT"
  }
}