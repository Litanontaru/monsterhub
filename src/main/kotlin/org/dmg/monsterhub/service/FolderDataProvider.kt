package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.FolderRepository
import org.springframework.stereotype.Service

@Service
class FolderDataProvider(
    val repository: FolderRepository
): SettingObjectDataProvider {
  override val objectClass: Class<*> = Folder::class.java

  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySetting(setting)

  override fun save(one: SettingObject) {
    repository.save(one as Folder)
  }

  override fun delete(one: SettingObject) {
    repository.delete(one as Folder)
  }
}