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
      parent
          ?.let { repository.findAllByParentAndHiddenFalse(it) }
          ?: run { repository.findAllBySettingAndParentIsNullAndHiddenFalse(setting) }


  override fun countChildrenAlikeBySetting(parent: Folder?, search: String, setting: Setting) =
      parent
          ?.let { repository.countByParentAndHiddenFalse(it) }
          ?: run { repository.countBySettingAndParentIsNullAndHiddenFalse(setting) }

  override fun create(): SettingObject = Folder()
}