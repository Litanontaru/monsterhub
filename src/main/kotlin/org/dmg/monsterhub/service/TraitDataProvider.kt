package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Trait
import org.dmg.monsterhub.data.Trait.Companion.TRAIT
import org.dmg.monsterhub.repository.TraitRepository
import org.springframework.stereotype.Service

@Service
class TraitDataProvider(
    repository: TraitRepository
) : SimpleSettingObjectDataProvider<Trait>(TRAIT, repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory("Черта") {
    Trait().apply { featureType = TRAIT }
  })
}