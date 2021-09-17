package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.FolderRepository
import org.springframework.stereotype.Service

@Service
class FolderDataProvider(
    repository: FolderRepository
) : SimpleSettingObjectDataProvider<Folder>(Folder::class.java, "FOLDER", "Папка", repository) {

  override fun getChildrenAlikeBySetting(parent: Folder?, search: String, setting: Setting) =
      when {
        search.isBlank() -> parent
            ?.let { repository.findAllByParentAndHiddenFalse(it) }
            ?: run { repository.findAllBySettingAndParentIsNullAndHiddenFalse(setting) }
        else -> listOf()
      }


  override fun countChildrenAlikeBySetting(parent: Folder?, search: String, setting: Setting) =
      when {
        search.isBlank() -> parent
            ?.let { repository.countByParentAndHiddenFalse(it) }
            ?: run { repository.countBySettingAndParentIsNullAndHiddenFalse(setting) }
        else -> 0
      }

  override fun create(): SettingObject = Folder()
}