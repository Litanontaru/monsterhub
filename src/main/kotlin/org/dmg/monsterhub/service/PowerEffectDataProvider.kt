package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.PowerEffect
import org.dmg.monsterhub.data.PowerEffect.Companion.POWER_EFFECT
import org.dmg.monsterhub.repository.PowerEffectRepository
import org.springframework.stereotype.Service

@Service
class PowerEffectDataProvider(
  dependencyAnalyzer: DependencyAnalyzer,
  repository: PowerEffectRepository
) : SimpleSettingObjectDataProvider<PowerEffect>(POWER_EFFECT, dependencyAnalyzer, repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory(POWER_EFFECT, "Эффект Силы") {
    PowerEffect().apply { featureType = POWER_EFFECT }
  })
}