package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Armor.Companion.ARMOR
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.Perk.Companion.PERK
import org.dmg.monsterhub.data.Skill.Companion.SKILL
import org.dmg.monsterhub.data.Trait.Companion.TRAIT
import org.dmg.monsterhub.data.Weapon.Companion.WEAPON
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.CreatureRepository
import org.springframework.stereotype.Service

@Service
class CreatureDataProvider(
    repository: CreatureRepository
) : SimpleSettingObjectDataProvider<Creature>(repository), FeatureContainerService {
  override val objectClass: Class<*> = Creature::class.java

  override val type: String = "CREATURE"

  override val name: String = "Существо"

  override fun create(): SettingObject = Creature().apply { featureType = type }

  override fun containerMeta(obj: FeatureContainerData): FeatureContainer = CreatureMeta

  private object CreatureMeta : FeatureContainer {
    override val containFeatureTypes: MutableList<FeatureContainerItem> = mutableListOf(
        FeatureContainerItem().apply {
          name = "Черты"
          featureType = TRAIT

        },
        FeatureContainerItem().apply {
          name = "Оружие"
          featureType = WEAPON

        },
        FeatureContainerItem().apply {
          name = "Броня"
          featureType = ARMOR
          onlyOne = true
        },
        FeatureContainerItem().apply {
          name = "Способности"
          featureType = SKILL
        },
        FeatureContainerItem().apply {
          name = "Перки"
          featureType = PERK
        }
    )
  }
}