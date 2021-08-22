package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.FreeFeature
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface FreeFeatureRepository: JpaRepository<FreeFeature, Long> {
  fun findAllBySetting(setting: Setting): List<FreeFeature>

  fun findAllByNameContainingAndSettingIn(name: String, settings: List<Setting>, pageable: Pageable): List<FreeFeature>

  fun countByNameContainingAndSettingIn(name: String, settings: List<Setting>): Int
}