package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.FreeFeature
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.domain.Pageable

interface FreeFeatureRepository : SettingObjectRepository<FreeFeature> {
  fun findAllByFeatureTypeAndNameContainingAndSettingIn(type: String, name: String, settings: List<Setting>, pageable: Pageable): List<FreeFeature>

  fun countByFeatureTypeAndNameContainingAndSettingIn(type: String, name: String, settings: List<Setting>): Int
}