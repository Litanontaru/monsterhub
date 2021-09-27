package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.Creature
import org.springframework.data.jpa.repository.Query

interface CreatureRepository : SettingObjectRepository<Creature> {
  @Query(value = "SELECT creature_id FROM base_creature WHERE base_id = :creatureId", nativeQuery = true)
  fun getSubCreatures(creatureId: Long): List<Long>
}