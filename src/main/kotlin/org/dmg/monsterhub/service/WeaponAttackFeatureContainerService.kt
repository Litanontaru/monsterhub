package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.WeaponAttack
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.springframework.stereotype.Service

@Service
class WeaponAttackFeatureContainerService: FeatureContainerService {
  override val objectClass: Class<*> = WeaponAttack::class.java

  override fun containerMeta(obj: FeatureContainerData): FeatureContainer = Meta

  private object Meta: FeatureContainer {
    override val containFeatureTypes: MutableList<FeatureContainerItem> = mutableListOf(
        FeatureContainerItem().apply {
          name = "Свойства"
          featureType = "WEAPON_ATTACK_FEATURE"
        }
    )
  }
}