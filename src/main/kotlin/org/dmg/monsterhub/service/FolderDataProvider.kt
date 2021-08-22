package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.FolderRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class FolderDataProvider(
    val repository: FolderRepository
) : SimpleSettingObjectDataProvider() {
  override val objectClass: Class<*> = Folder::class.java

  override val type: String = "FOLDER"

  override val name: String = "Папка"

  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySetting(setting)

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable): List<SettingObject> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>): Int {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun refresh(one: SettingObject) = repository.getById(one.id)

  override fun save(one: SettingObject) {
    repository.save(one as Folder)
  }

  override fun delete(one: SettingObject) {
    repository.delete(one as Folder)
  }

  override fun create(): SettingObject = Folder()
}