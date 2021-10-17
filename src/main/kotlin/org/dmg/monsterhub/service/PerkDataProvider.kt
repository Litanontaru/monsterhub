package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Perk
import org.dmg.monsterhub.data.Perk.Companion.PERK
import org.dmg.monsterhub.repository.PerkRepository
import org.springframework.stereotype.Service

@Service
class PerkDataProvider(
    repository: PerkRepository
) : SimpleSettingObjectDataProvider<Perk>(PERK, repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory("Перк") {
    Perk().apply { featureType = PERK }
  })
}