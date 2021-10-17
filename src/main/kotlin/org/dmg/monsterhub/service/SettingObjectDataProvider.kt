package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.springframework.data.domain.Pageable

interface SettingObjectDataProvider {
  fun supportType(type: String): Boolean

  fun getById(id: Long): SettingObject?

  fun getChildrenAlikeBySetting(parent: Folder?, search: String, setting: Setting): List<SettingObject>

  fun countChildrenAlikeBySetting(parent: Folder?, search: String, setting: Setting): Int

  fun hasChildrenAlikeBySetting(parent: Folder?, setting: Setting): Boolean

  fun getBySettings(type: String, settings: List<Setting>, pageable: Pageable): List<SettingObject>

  fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable): List<SettingObject>

  fun countBySettings(type: String, settings: List<Setting>): Int

  fun countAlikeBySettings(type: String, name: String, settings: List<Setting>): Int

  fun save(one: SettingObject): SettingObject

  fun factories(): List<SettingObjectFactory> = listOf()
}