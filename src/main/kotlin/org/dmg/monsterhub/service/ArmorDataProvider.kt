package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Armor
import org.dmg.monsterhub.data.Armor.Companion.ARMOR
import org.dmg.monsterhub.repository.ArmorRepository
import org.springframework.stereotype.Service

@Service
class ArmorDataProvider(
    repository: ArmorRepository
) : SimpleSettingObjectDataProvider<Armor>(ARMOR, repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory(ARMOR, "Броня") {
    Armor().apply { featureType = ARMOR }
  })
}