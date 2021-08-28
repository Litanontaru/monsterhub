package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.PowerEffect
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.PowerEffectRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PowerEffectDataProvider(
    val repository: PowerEffectRepository
) : SimpleSettingObjectDataProvider() {
  override val objectClass: Class<*> = PowerEffect::class.java

  override val type: String = POWER_EFFECT

  override val name: String = "Эффект Силы"

  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySetting(setting)

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByNameContainingAndSettingIn(name, settings, pageable)

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>) =
      repository.countByNameContainingAndSettingIn(name, settings)

  override fun refresh(one: SettingObject) = repository.getById(one.id)

  override fun save(one: SettingObject) {
    repository.save(one as PowerEffect)
  }

  override fun delete(one: SettingObject) {
    repository.delete(one as PowerEffect)
  }

  override fun create(): SettingObject = PowerEffect().apply { featureType = POWER_EFFECT }

  companion object {
    val POWER_EFFECT = "POWER_EFFECT"
  }
}