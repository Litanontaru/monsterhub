package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.Skill
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface SkillRepository: JpaRepository<Skill, Long> {
  fun findAllBySetting(setting: Setting): List<Skill>

  fun findAllByNameContainingAndSettingIn(name: String, settings: List<Setting>, pageable: Pageable): List<Skill>

  fun countByNameContainingAndSettingIn(name: String, settings: List<Setting>): Int
}