package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.PowerEffect
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PowerEffectRepository : JpaRepository<PowerEffect, Long> {
  fun findAllBySetting(setting: Setting): List<PowerEffect>

  fun findAllByNameContainingAndSettingIn(name: String, settings: List<Setting>, pageable: Pageable): List<PowerEffect>

  fun countByNameContainingAndSettingIn(name: String, settings: List<Setting>): Int
}