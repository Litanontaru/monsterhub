package org.dmg.monsterhub.model

import org.springframework.data.repository.CrudRepository

interface CreatureRepository: CrudRepository<Creature, Long> {
    fun findByName(name: String): Creature?
}