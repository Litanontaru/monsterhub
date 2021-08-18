package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.Perk
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PerkRepository: JpaRepository<Perk, Long> {
  fun findAllBySetting(setting: Setting): List<Perk>

  fun findAllByNameContainingAndSettingIn(name: String, settings: List<Setting>, pageable: Pageable): List<Perk>

  fun countByNameContainingAndSettingIn(name: String, settings: List<Setting>): Int
}