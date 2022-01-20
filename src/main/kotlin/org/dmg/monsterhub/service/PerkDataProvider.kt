package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Perk
import org.dmg.monsterhub.data.Perk.Companion.PERK
import org.dmg.monsterhub.repository.PerkRepository
import org.springframework.stereotype.Service

@Service
class PerkDataProvider(
  dependencyAnalyzer: DependencyAnalyzer,
  repository: PerkRepository
) : SimpleSettingObjectDataProvider<Perk>(PERK, dependencyAnalyzer, repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory(PERK, "Перк") {
    Perk().apply { featureType = PERK }
  })
}