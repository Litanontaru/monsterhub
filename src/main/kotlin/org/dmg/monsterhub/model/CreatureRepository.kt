package org.dmg.monsterhub.model

import org.springframework.data.repository.CrudRepository

interface CreatureRepository: CrudRepository<OldCreature, Long> {
    fun findByName(name: String): OldCreature?
}