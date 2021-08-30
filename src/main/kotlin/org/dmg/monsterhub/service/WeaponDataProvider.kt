package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.Weapon
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.WeaponRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class WeaponDataProvider(
    val repository: WeaponRepository
) : SimpleSettingObjectDataProvider(), FeatureContainerService {
  override val objectClass: Class<*> = Weapon::class.java

  override val type: String = "WEAPON"

  override val name: String = "Оружие"

  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySetting(setting)

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByNameContainingAndSettingIn(name, settings, pageable)

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>) =
      repository.countByNameContainingAndSettingIn(name, settings)

  override fun refresh(one: SettingObject) = repository.getById(one.id)

  override fun save(one: SettingObject) {
    repository.save(one as Weapon)
  }

  override fun delete(one: SettingObject) {
    repository.delete(one as Weapon)
  }

  override fun create(): SettingObject = Weapon().apply { featureType = type }

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