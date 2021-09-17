package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface SettingObjectRepository<T : SettingObject> : DBObjectRepository<T> {
  fun findAllByParentAndNameContainingAndHiddenFalse(parent: Folder, name: String): List<T>

  fun countByParentAndNameContainingAndHiddenFalse(parent: Folder, name: String): Int

  fun existsByParentAndHiddenFalse(parent: Folder): Boolean

  fun findAllBySettingAndNameContainingAndParentIsNullAndHiddenFalse(setting: Setting, name: String): List<T>

  fun countBySettingAndNameContainingAndParentIsNullAndHiddenFalse(setting: Setting, name: String): Int

  fun existsBySettingAndParentIsNullAndHiddenFalse(setting: Setting): Boolean

  fun findAllBySettingAndHiddenFalse(setting: Setting): List<T>

  fun findAllByNameContainingAndSettingIn(name: String, settings: List<Setting>, pageable: Pageable): List<T>

  fun countByNameContainingAndSettingIn(name: String, settings: List<Setting>): Int
}