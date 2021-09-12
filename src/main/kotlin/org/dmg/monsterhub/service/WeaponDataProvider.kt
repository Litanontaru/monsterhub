package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.Weapon
import org.dmg.monsterhub.data.Weapon.Companion.WEAPON
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.WeaponRepository
import org.springframework.stereotype.Service

@Service
class WeaponDataProvider(
    repository: WeaponRepository
) : SimpleSettingObjectDataProvider<Weapon>(repository), FeatureContainerService {
  override val objectClass: Class<*> = Weapon::class.java

  override val type: String = WEAPON

  override val name: String = "Оружие"

  override fun create(): SettingObject = Weapon().apply { featureType = WEAPON }

  override fun containerMeta(obj: FeatureContainerData): FeatureContainer = Meta

  private object Meta : FeatureContainer {
    override val containFeatureTypes: MutableList<FeatureContainerItem> = mutableListOf(
        FeatureContainerItem().apply {
          name = "Свойства"
          featureType = "WEAPON_FEATURE"
        }
    )
  }
}