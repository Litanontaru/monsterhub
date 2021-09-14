package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Armor
import org.dmg.monsterhub.data.Armor.Companion.ARMOR
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.ArmorRepository
import org.springframework.stereotype.Service

@Service
class ArmorDataProvider(
    repository: ArmorRepository
) : SimpleSettingObjectDataProvider<Armor>(Armor::class.java, ARMOR, "Броня", repository) {
  override fun create(): SettingObject = Armor().apply { featureType = ARMOR }
}