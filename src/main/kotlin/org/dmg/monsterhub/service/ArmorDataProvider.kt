package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Armor
import org.dmg.monsterhub.data.Armor.Companion.ARMOR
import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.ArmorRepository
import org.springframework.stereotype.Service

@Service
class ArmorDataProvider(
    repository: ArmorRepository
) : SimpleSettingObjectDataProvider<Armor>(repository), FeatureContainerService {
  override val objectClass: Class<*> = Armor::class.java

  override val type: String = ARMOR

  override val name: String = "Броня"

  override fun create(): SettingObject = Armor().apply { featureType = ARMOR }

  override fun containerMeta(obj: FeatureContainerData): FeatureContainer = Meta

  private object Meta : FeatureContainer {
    override val containFeatureTypes: MutableList<FeatureContainerItem> = mutableListOf(
        FeatureContainerItem().apply {
          name = "Свойства"
          featureType = "ARMOR_FEATURE"
        }
    )
  }
}