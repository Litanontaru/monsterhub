package org.dmg.monsterhub.model

import org.springframework.data.repository.CrudRepository

interface OldCreatureRepository : CrudRepository<OldCreature, Long> {
  fun findByName(name: String): OldCreature?
}