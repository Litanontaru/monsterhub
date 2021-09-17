package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.SettingObjectRepository
import org.dmg.monsterhub.repository.update
import org.dmg.monsterhub.repository.updateAsync
import org.springframework.data.domain.Pageable

abstract class AbstractSettingObjectDataProvider<T : SettingObject>(
    override val objectClass: Class<*>,
    open val repository: SettingObjectRepository<T>
) : SettingObjectDataProvider {
  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySettingAndHiddenFalse(setting)

  override fun getChildrenAlikeBySetting(parent: Folder?, search: String, setting: Setting) =
      parent
          ?.let { repository.findAllByParentAndNameContainingAndHiddenFalse(it, search) }
          ?: run { repository.findAllBySettingAndNameContainingAndParentIsNullAndHiddenFalse(setting, search) }

  override fun countChildrenAlikeBySetting(parent: Folder?, search: String, setting: Setting) =
      parent
          ?.let { repository.countByParentAndNameContainingAndHiddenFalse(it, search) }
          ?: run { repository.countBySettingAndNameContainingAndParentIsNullAndHiddenFalse(setting, search) }

  override fun hasChildrenAlikeBySetting(parent: Folder?, setting: Setting) =
      parent
          ?.let { repository.existsByParentAndHiddenFalse(it) }
          ?: run { repository.existsBySettingAndParentIsNullAndHiddenFalse(setting) }

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByNameContainingAndSettingIn(name, settings, pageable)

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>) =
      repository.countByNameContainingAndSettingIn(name, settings)

  override fun refresh(one: SettingObject) = repository.getById(one.id)

  override fun saveAsync(one: SettingObject) = repository.updateAsync(one).thenApply { it as SettingObject }

  override fun save(one: SettingObject) = repository.update(one) as SettingObject
}