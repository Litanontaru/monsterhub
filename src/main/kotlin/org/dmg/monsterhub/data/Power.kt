package org.dmg.monsterhub.data

import org.dmg.monsterhub.service.PowerEffectDataProvider.Companion.POWER_EFFECT
import javax.persistence.Entity

@Entity
class Power : ContainerData(), SkillLike {
  override var skillType: SkillType = SkillType.OFFENSE

  private fun calculator(): PowerRateCalculator = features
      .find { it.feature.featureType == POWER_EFFECT }
      ?.let { it.feature as PowerEffect }
      ?.let { it.calculator }
      ?: PowerRateCalculator.STANDARD

  override fun rate() = calculator().calculator(this)
}