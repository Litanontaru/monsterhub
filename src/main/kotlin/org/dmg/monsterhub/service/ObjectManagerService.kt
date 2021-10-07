package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.SettingObject

interface ObjectManagerService {
  fun create(featureType: String): SettingObject
  fun update(settingObject: SettingObject): SettingObject
}