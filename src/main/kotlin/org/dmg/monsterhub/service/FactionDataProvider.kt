package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Faction
import org.dmg.monsterhub.data.Faction.Companion.FACTION
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.FactionRepository
import org.springframework.stereotype.Service

@Service
class FactionDataProvider(
    repository: FactionRepository
) : SimpleSettingObjectDataProvider<Faction>(Faction::class.java, FACTION, "Фракция", repository) {
  override fun create(): SettingObject = Faction().apply { featureType = FACTION }
}