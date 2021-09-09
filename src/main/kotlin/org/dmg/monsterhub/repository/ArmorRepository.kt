package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.Armor
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ArmorRepository : JpaRepository<Armor, Long> {
  fun findAllBySetting(setting: Setting): List<Armor>

  fun findAllByNameContainingAndSettingIn(name: String, settings: List<Setting>, pageable: Pageable): List<Armor>

  fun countByNameContainingAndSettingIn(name: String, settings: List<Setting>): Int

  fun findAllByNameInAndSettingIn(names: List<String>, settings: List<Setting>): List<Armor>
}