package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Faction
import org.dmg.monsterhub.data.Faction.Companion.FACTION
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.FactionRepository
import org.springframework.stereotype.Service

@Service
class FactionDataProvider(
    repository: FactionRepository
) : SimpleSettingObjectDataProvider<Faction>(FACTION, "Фракция", repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory("Фракция") {
    Faction().apply { featureType = FACTION }
  })

  override fun create(): SettingObject = Faction().apply { featureType = FACTION }
}