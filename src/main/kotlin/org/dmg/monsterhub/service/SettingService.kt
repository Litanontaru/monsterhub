package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.repository.SettingRepository
import org.dmg.monsterhub.repository.updateAsync
import org.springframework.stereotype.Service

@Service
class SettingService(
    val repository: SettingRepository
) {
  fun get(id: Long) = repository.getById(id)

  fun save(setting: Setting) {
    repository.updateAsync(setting)
  }
}