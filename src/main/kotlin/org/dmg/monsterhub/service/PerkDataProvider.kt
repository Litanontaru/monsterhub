package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Perk
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.PerkRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PerkDataProvider(
    val repository: PerkRepository
) : SettingObjectDataProvider {
  override val objectClass: Class<*> = Perk::class.java

  override val type: String = "PERK"

  override val name: String = "Перк"

  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySetting(setting)

  override fun getAlikeBySettings(name: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByNameContainingAndSettingIn(name, settings, pageable)

  override fun countAlikeBySettings(name: String, settings: List<Setting>) =
      repository.countByNameContainingAndSettingIn(name, settings)

  override fun save(one: SettingObject) {
    repository.save(one as Perk)
  }

  override fun delete(one: SettingObject) {
    repository.delete(one as Perk)
  }

  override fun create(): SettingObject = Perk().apply { featureType = type }
}