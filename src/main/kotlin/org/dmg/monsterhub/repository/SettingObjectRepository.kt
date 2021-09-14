package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface SettingObjectRepository<T : SettingObject> : DBObjectRepository<T> {
  fun findAllBySettingAndHiddenFalse(setting: Setting): List<T>

  fun findAllByNameContainingAndSettingIn(name: String, settings: List<Setting>, pageable: Pageable): List<T>

  fun countByNameContainingAndSettingIn(name: String, settings: List<Setting>): Int
}