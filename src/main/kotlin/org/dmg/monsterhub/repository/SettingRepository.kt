package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.setting.Setting

interface SettingRepository : SettingObjectRepository<Setting> {
  fun findAllByNameContaining(name: String): List<Setting>

  fun countByNameContaining(name: String): Int
}