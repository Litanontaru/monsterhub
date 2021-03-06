package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Faction
import org.dmg.monsterhub.data.Faction.Companion.FACTION
import org.dmg.monsterhub.repository.FactionRepository
import org.springframework.stereotype.Service

@Service
class FactionDataProvider(
  dependencyAnalyzer: DependencyAnalyzer,
  repository: FactionRepository
) : SimpleSettingObjectDataProvider<Faction>(FACTION, dependencyAnalyzer, repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory(FACTION, "Фракция") {
    Faction().apply { featureType = FACTION }
  })
}