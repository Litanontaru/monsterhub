package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.Setting.Companion.SETTING
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.SettingRepository
import org.springframework.stereotype.Service

@Service
class SettingDataProvider(
    repository: SettingRepository
) : SimpleSettingObjectDataProvider<Setting>(Setting::class.java, SETTING, "Игровой мир", repository) {
  override fun canCreate(): Boolean = false

  override fun create(): SettingObject = Setting().apply { featureType = SETTING }
}