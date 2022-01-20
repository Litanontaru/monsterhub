package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Trait
import org.dmg.monsterhub.data.Trait.Companion.TRAIT
import org.dmg.monsterhub.repository.TraitRepository
import org.springframework.stereotype.Service

@Service
class TraitDataProvider(
  dependencyAnalyzer: DependencyAnalyzer,
  repository: TraitRepository
) : SimpleSettingObjectDataProvider<Trait>(TRAIT, dependencyAnalyzer, repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory(TRAIT, "Черта") {
    Trait().apply { featureType = TRAIT }
  })
}