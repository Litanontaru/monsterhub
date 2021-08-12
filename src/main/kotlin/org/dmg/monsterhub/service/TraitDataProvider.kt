package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Trait
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.TraitRepository
import org.springframework.stereotype.Service

@Service
class TraitDataProvider(
    val repository: TraitRepository
    ): SettingObjectDataProvider {
  override val objectClass: Class<*> = Trait::class.java

  override val name: String = "Черта"

  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySetting(setting)

  override fun save(one: SettingObject) {
    repository.save(one as Trait)
  }

  override fun delete(one: SettingObject) {
    repository.delete(one as Trait)
  }

  override fun create(): SettingObject = Trait()
}