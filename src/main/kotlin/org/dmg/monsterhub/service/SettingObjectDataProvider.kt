package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject

interface SettingObjectDataProvider {
  val objectClass: Class<*>

  fun getAllBySetting(setting: Setting): List<SettingObject>

  fun save(one: SettingObject)

  fun delete(one: SettingObject)
}