package org.dmg.monsterhub.model

import org.springframework.stereotype.Service

@Service
class CreatureService(
        val repository: CreatureRepository
) {
    fun save(creature: Creature) {
        repository.save(creature)
    }

    fun find(name: String): Creature? = repository.findByName(name)
}