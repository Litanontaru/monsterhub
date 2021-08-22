package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Trait
import org.dmg.monsterhub.data.Trait.Companion.TRAIT
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.TraitRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class TraitDataProvider(
    val repository: TraitRepository
) : SimpleSettingObjectDataProvider() {
  override val objectClass: Class<*> = Trait::class.java

  override val type: String = TRAIT

  override val name: String = "Черта"

  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySetting(setting)

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByNameContainingAndSettingIn(name, settings, pageable)

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>) =
      repository.countByNameContainingAndSettingIn(name, settings)

  override fun refresh(one: SettingObject) = repository.getById(one.id)

  override fun save(one: SettingObject) {
    repository.save(one as Trait)
  }

  override fun delete(one: SettingObject) {
    repository.delete(one as Trait)
  }

  override fun create(): SettingObject = Trait().apply { featureType = type }
}