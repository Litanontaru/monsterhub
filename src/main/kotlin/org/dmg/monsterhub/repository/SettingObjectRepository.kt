package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface SettingObjectRepository<T : SettingObject> : DBObjectRepository<T> {
  fun findAllByParentAndHiddenFalse(parent: Folder): List<T>

  fun countByParentAndHiddenFalse(parent: Folder): Int

  fun existsByParentAndHiddenFalse(parent: Folder): Boolean

  fun findAllBySettingAndNameContainingAndHiddenFalse(setting: Setting, name: String): List<T>

  fun countBySettingAndNameContainingAndHiddenFalse(setting: Setting, name: String): Int

  fun findAllBySettingAndParentIsNullAndHiddenFalse(setting: Setting): List<T>

  fun countBySettingAndParentIsNullAndHiddenFalse(setting: Setting): Int

  fun existsBySettingAndParentIsNullAndHiddenFalse(setting: Setting): Boolean

  fun findAllBySettingAndHiddenFalse(setting: Setting): List<T>

  fun findAllBySettingIn(settings: List<Setting>, pageable: Pageable): List<T>

  fun findAllByNameContainingAndSettingIn(name: String, settings: List<Setting>, pageable: Pageable): List<T>

  fun countBySettingIn(settings: List<Setting>): Int

  fun countByNameContainingAndSettingIn(name: String, settings: List<Setting>): Int
}