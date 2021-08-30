package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.Weapon
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface WeaponRepository : JpaRepository<Weapon, Long> {
  fun findAllBySetting(setting: Setting): List<Weapon>

  fun findAllByNameContainingAndSettingIn(name: String, settings: List<Setting>, pageable: Pageable): List<Weapon>

  fun countByNameContainingAndSettingIn(name: String, settings: List<Setting>): Int
}