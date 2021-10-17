package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.Creature.Companion.CREATURE
import org.dmg.monsterhub.data.Creature.Companion.CREATURE_TYPES
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.repository.CreatureRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CreatureDataProvider(
    override val repository: CreatureRepository
) : AbstractSettingObjectDataProvider<Creature>(repository) {

  private fun mapType(type: String): List<String> = when (type) {
    CREATURE -> CREATURE_TYPES
    else -> listOf(type)
  }

  override fun getBySettings(type: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByFeatureTypeInAndSettingIn(mapType(type), settings, pageable)

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByFeatureTypeInAndNameContainingAndSettingIn(mapType(type), name, settings, pageable)

  override fun countBySettings(type: String, settings: List<Setting>) =
      repository.countByFeatureTypeInAndSettingIn(mapType(type), settings)

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>) =
      repository.countByFeatureTypeInAndNameContainingAndSettingIn(mapType(type), name, settings)

  override fun supportType(type: String) = CREATURE_TYPES.contains(type)

  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory("Существо") {
    Creature().apply { featureType = CREATURE }
  })
}