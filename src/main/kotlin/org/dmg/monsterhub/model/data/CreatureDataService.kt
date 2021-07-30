package org.dmg.monsterhub.model.data

import org.dmg.monsterhub.model.yaml.CreatureLoader
import org.springframework.stereotype.Service

@Service
class CreatureDataService(
        val repository: CreatureDataRepository,
        val loader: CreatureLoader
) {
    fun save(yamlCreature: String) {
        val yCreature = loader(yamlCreature)

        (repository.findById(yCreature.name).orElse(null) ?: CreatureData().also { it.name = yCreature.name })
                .also {
                    it.base = yCreature.base.toMutableList()
                    it.traits = yCreature.traits.toMutableList()
                }
                .apply { repository.save(this) }
    }
}