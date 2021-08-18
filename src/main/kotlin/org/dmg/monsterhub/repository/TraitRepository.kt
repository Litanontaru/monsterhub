package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.Trait
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface TraitRepository : JpaRepository<Trait, Long> {
  fun findAllBySetting(setting: Setting): List<Trait>

  fun findAllByNameContainingAndSettingIn(name: String, settings: List<Setting>, pageable: Pageable): List<Trait>

  fun countByNameContainingAndSettingIn(name: String, settings: List<Setting>): Int
}