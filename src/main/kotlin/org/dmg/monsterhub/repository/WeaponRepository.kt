package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.Weapon
import org.dmg.monsterhub.data.setting.Setting

interface WeaponRepository : SettingObjectRepository<Weapon> {
  fun findAllByNameInAndSettingIn(names: List<String>, settings: List<Setting>): List<Weapon>
}