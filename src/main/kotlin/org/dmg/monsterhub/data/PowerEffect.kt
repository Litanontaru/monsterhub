package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.Feature
import javax.persistence.Entity

@Entity
class PowerEffect: Feature() {
  var calculator: PowerRateCalculator = PowerRateCalculator.STANDARD

  companion object {
    val POWER_EFFECT = "POWER_EFFECT"
  }
}