package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Folder.Companion.FOLDER
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.pages.SettingObjectTreeFilter
import org.dmg.monsterhub.repository.FolderRepository
import org.springframework.stereotype.Service

@Service
class FolderDataProvider(
  dependencyAnalyzer: DependencyAnalyzer,
  repository: FolderRepository
) : SimpleSettingObjectDataProvider<Folder>(FOLDER, dependencyAnalyzer, repository) {

  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory(FOLDER, "Папка") {
    Folder().apply { featureType = FOLDER }
  })

  override fun getChildrenAlikeBySetting(parent: Folder?, filter: SettingObjectTreeFilter, setting: Setting) =
    when {
      filter.hasFilter() -> listOf()
      filter.hasFindUsages() -> listOf()
      else -> parent
        ?.let { repository.findAllByParentAndHiddenFalse(it) }
        ?: run { repository.findAllBySettingAndParentIsNullAndHiddenFalse(setting) }
    }


  override fun countChildrenAlikeBySetting(parent: Folder?, filter: SettingObjectTreeFilter, setting: Setting) =
    when {
      filter.hasFilter() -> 0
      filter.hasFindUsages() -> 0
      else -> parent
        ?.let { repository.countByParentAndHiddenFalse(it) }
        ?: run { repository.countBySettingAndParentIsNullAndHiddenFalse(setting) }

    }
}