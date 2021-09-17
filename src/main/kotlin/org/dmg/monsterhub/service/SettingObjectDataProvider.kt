package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.springframework.data.domain.Pageable
import java.util.concurrent.CompletableFuture

interface SettingObjectDataProvider {
  val objectClass: Class<*>

  fun supportType(type: String): Boolean

  val name: String

  fun getAllBySetting(setting: Setting): List<SettingObject>

  fun getChildrenAlikeBySetting(parent: Folder?, search: String, setting: Setting): List<SettingObject>

  fun countChildrenAlikeBySetting(parent: Folder?, search: String, setting: Setting): Int

  fun hasChildrenAlikeBySetting(parent: Folder?, setting: Setting): Boolean

  fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable): List<SettingObject>

  fun countAlikeBySettings(type: String, name: String, settings: List<Setting>): Int

  fun refresh(one: SettingObject): SettingObject

  fun saveAsync(one: SettingObject): CompletableFuture<SettingObject>

  fun save(one: SettingObject): SettingObject

  fun canCreate(): Boolean = true

  fun create(): SettingObject
}