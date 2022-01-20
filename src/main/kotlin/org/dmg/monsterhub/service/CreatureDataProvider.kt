package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.Creature.Companion.CREATURE
import org.dmg.monsterhub.data.Creature.Companion.CREATURE_RACE
import org.dmg.monsterhub.data.Creature.Companion.CREATURE_RACE_TEMPLATE
import org.dmg.monsterhub.data.Creature.Companion.CREATURE_REPRESENTATIVE
import org.dmg.monsterhub.data.Creature.Companion.CREATURE_TYPES
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.repository.CreatureRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CreatureDataProvider(
  dependencyAnalyzer: DependencyAnalyzer,
  override val repository: CreatureRepository
) : AbstractSettingObjectDataProvider<Creature>(dependencyAnalyzer, repository) {

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

  override fun groupFactories(): String = "Существо"

  override fun factories(): List<SettingObjectFactory> = CREATURE_FACTORIES

  companion object {
    val ALL_CREATURE_TYPES = listOf(
        CREATURE to "Существо (deprecated)",
        CREATURE_RACE_TEMPLATE to "Шаблон расы",
        CREATURE_RACE to "Раса",
        CREATURE_REPRESENTATIVE to "Представитель расы"
    )

    val CREATURE_FACTORIES = ALL_CREATURE_TYPES
        .filter { it.first != CREATURE }
        .map { SettingObjectFactory(it.first, it.second) { Creature().apply { featureType = it.first } } }
  }
}