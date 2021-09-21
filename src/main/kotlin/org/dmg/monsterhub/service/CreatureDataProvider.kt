package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.Creature.Companion.CREATURE
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.CreatureRepository
import org.springframework.stereotype.Service

@Service
class CreatureDataProvider(
    repository: CreatureRepository
) : SimpleSettingObjectDataProvider<Creature>(Creature::class.java, CREATURE, "Существо", repository) {
  override fun create(): SettingObject = Creature().apply { featureType = type }
}