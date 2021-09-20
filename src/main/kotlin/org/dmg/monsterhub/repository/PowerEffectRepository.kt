package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.PowerEffect
import org.dmg.monsterhub.data.setting.Setting

interface PowerEffectRepository : SettingObjectRepository<PowerEffect> {
  fun findAllByNameAndSettingIn(name: String, settings: List<Setting>): List<PowerEffect>
}