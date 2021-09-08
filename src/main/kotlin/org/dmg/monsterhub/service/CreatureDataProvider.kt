package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Armor.Companion.ARMOR
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.Trait.Companion.TRAIT
import org.dmg.monsterhub.data.Weapon
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.CreatureRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CreatureDataProvider(
    val repository: CreatureRepository
) : SimpleSettingObjectDataProvider(), FeatureContainerService {
  override val objectClass: Class<*> = Creature::class.java

  override val type: String = "CREATURE"

  override val name: String = "Существо"

  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySetting(setting)

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByNameContainingAndSettingIn(name, settings, pageable)

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>) =
      repository.countByNameContainingAndSettingIn(name, settings)

  override fun refresh(one: SettingObject) = repository.getById(one.id)

  override fun save(one: SettingObject) {
    repository.save(one as Creature)
  }

  override fun delete(one: SettingObject) {
    repository.delete(one as Creature)
  }

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
          featureType = Weapon.WEAPON

        },
        FeatureContainerItem().apply {
          name = "Броня"
          featureType = ARMOR
          onlyOne = true
        }
    )
  }
}