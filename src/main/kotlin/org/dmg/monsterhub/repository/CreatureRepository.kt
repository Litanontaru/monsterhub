package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query

interface CreatureRepository : SettingObjectRepository<Creature> {
  fun findAllByFeatureTypeInAndSettingIn(type: List<String>, settings: List<Setting>, pageable: Pageable): List<Creature>

  fun findAllByFeatureTypeInAndNameContainingAndSettingIn(type: List<String>, name: String, settings: List<Setting>, pageable: Pageable): List<Creature>

  fun countByFeatureTypeInAndSettingIn(type: List<String>, settings: List<Setting>): Int

  fun countByFeatureTypeInAndNameContainingAndSettingIn(type: List<String>, name: String, settings: List<Setting>): Int

  @Query(value = "SELECT creature_id FROM base_creature WHERE base_id = :creatureId", nativeQuery = true)
  fun getSubCreatures(creatureId: Long): List<Long>
}