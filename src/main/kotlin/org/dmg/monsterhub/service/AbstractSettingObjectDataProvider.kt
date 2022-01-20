package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.SettingObjectTreeFilter
import org.dmg.monsterhub.repository.SettingObjectRepository
import org.dmg.monsterhub.repository.update
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull

abstract class AbstractSettingObjectDataProvider<T : SettingObject>(
  val dependencyAnalyzer: DependencyAnalyzer,
  open val repository: SettingObjectRepository<T>
) : SettingObjectDataProvider {

  override fun getById(id: Long): SettingObject? = repository.findByIdOrNull(id)

  override fun getChildrenAlikeBySetting(parent: Folder?, filter: SettingObjectTreeFilter, setting: Setting) =
    when {
      filter.hasFilter() -> repository.findAllBySettingAndNameContainingAndHiddenFalse(setting, filter.filterValue())
      filter.hasFindUsages() -> repository.findAllBySettingAndIdInAndHiddenFalse(
        setting,
        dependencyAnalyzer.findUsages(filter.findUsages().id)
      )
      else -> parent
        ?.let { repository.findAllByParentAndHiddenFalse(it) }
        ?: run { repository.findAllBySettingAndParentIsNullAndHiddenFalse(setting) }
    }


  override fun countChildrenAlikeBySetting(parent: Folder?, filter: SettingObjectTreeFilter, setting: Setting) =
    when {
      filter.hasFilter() -> repository.countBySettingAndNameContainingAndHiddenFalse(setting, filter.filterValue())
      filter.hasFindUsages() -> repository.countBySettingAndIdInAndHiddenFalse(
        setting,
        dependencyAnalyzer.findUsages(filter.findUsages().id)
      )
      else -> parent
        ?.let { repository.countByParentAndHiddenFalse(it) }
        ?: run { repository.countBySettingAndParentIsNullAndHiddenFalse(setting) }
    }

  override fun hasChildrenAlikeBySetting(parent: Folder?, setting: Setting) =
    parent
      ?.let { repository.existsByParentAndHiddenFalse(it) }
      ?: run { repository.existsBySettingAndParentIsNullAndHiddenFalse(setting) }

  override fun getBySettings(type: String, settings: List<Setting>, pageable: Pageable) =
    repository.findAllBySettingIn(settings, pageable)

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable) =
    repository.findAllByNameContainingAndSettingIn(name, settings, pageable)

  override fun countBySettings(type: String, settings: List<Setting>) =
    repository.countBySettingIn(settings)

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>) =
    repository.countByNameContainingAndSettingIn(name, settings)

  override fun save(one: SettingObject) = repository.update(one) as SettingObject
}