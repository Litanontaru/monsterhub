package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.Trait
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.jpa.repository.JpaRepository

interface TraitRepository: JpaRepository<Trait, Long> {
  fun findAllBySetting(setting: Setting): List<Trait>
}