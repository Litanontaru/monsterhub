package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.FolderRepository
import org.springframework.stereotype.Service

@Service
class FolderDataProvider(
    repository: FolderRepository
) : SimpleSettingObjectDataProvider<Folder>(repository) {
  override val objectClass: Class<*> = Folder::class.java

  override val type: String = "FOLDER"

  override val name: String = "Папка"

  override fun delete(one: SettingObject) {
    repository.delete(one as Folder)
  }

  override fun create(): SettingObject = Folder()
}