package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.springframework.data.domain.Pageable

interface SettingObjectDataProvider {
  val objectClass: Class<*>

  fun supportType(type: String): Boolean

  val name: String

  fun getAllBySetting(setting: Setting): List<SettingObject>

  fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable): List<SettingObject>

  fun countAlikeBySettings(type: String, name: String, settings: List<Setting>): Int

  fun refresh(one: SettingObject): SettingObject

  fun save(one: SettingObject)

  fun delete(one: SettingObject)

  fun create(): SettingObject
}