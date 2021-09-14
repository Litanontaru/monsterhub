package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.SettingObjectRepository
import org.dmg.monsterhub.repository.update
import org.dmg.monsterhub.repository.updateAsunc
import org.springframework.data.domain.Pageable

abstract class AbstractSettingObjectDataProvider<T : SettingObject>(
    override val objectClass: Class<*>,
    open val repository: SettingObjectRepository<T>
) : SettingObjectDataProvider {
  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySettingAndHiddenFalse(setting)

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByNameContainingAndSettingIn(name, settings, pageable)

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>) =
      repository.countByNameContainingAndSettingIn(name, settings)

  override fun refresh(one: SettingObject) = repository.getById(one.id)

  override fun saveAsync(one: SettingObject) = repository.updateAsunc(one).thenApply { it as SettingObject }

  override fun save(one: SettingObject) = repository.update(one) as SettingObject
}