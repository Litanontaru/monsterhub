package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.FolderRepository
import org.springframework.stereotype.Service

@Service
class FolderDataProvider(
    repository: FolderRepository
) : SimpleSettingObjectDataProvider<Folder>(Folder::class.java, "FOLDER", "Папка", repository) {
  override fun create(): SettingObject = Folder()
}