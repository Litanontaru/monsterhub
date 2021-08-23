package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PowerRepository : JpaRepository<Power, Long> {
  fun findAllBySetting(setting: Setting): List<Power>

  fun findAllByNameContainingAndSettingIn(name: String, settings: List<Setting>, pageable: Pageable): List<Power>

  fun countByNameContainingAndSettingIn(name: String, settings: List<Setting>): Int
}