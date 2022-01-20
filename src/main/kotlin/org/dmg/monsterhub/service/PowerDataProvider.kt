package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.data.Power.Companion.POWER
import org.dmg.monsterhub.repository.PowerRepository
import org.springframework.stereotype.Service

@Service
class PowerDataProvider(
  dependencyAnalyzer: DependencyAnalyzer,
  repository: PowerRepository
) : SimpleSettingObjectDataProvider<Power>(POWER, dependencyAnalyzer, repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory(POWER, "Сила") {
    Power().apply { featureType = POWER }
  })
}