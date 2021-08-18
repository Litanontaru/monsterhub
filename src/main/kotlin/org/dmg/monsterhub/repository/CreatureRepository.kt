package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CreatureRepository : JpaRepository<Creature, Long> {
  fun findAllBySetting(setting: Setting): List<Creature>

  fun findAllByNameContainingAndSettingIn(name: String, settings: List<Setting>, pageable: Pageable): List<Creature>

  fun countByNameContainingAndSettingIn(name: String, settings: List<Setting>): Int
}