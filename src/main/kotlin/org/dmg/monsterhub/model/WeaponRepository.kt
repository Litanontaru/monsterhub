package org.dmg.monsterhub.model

import org.springframework.data.repository.CrudRepository

interface WeaponRepository: CrudRepository<Weapon, Long> {
    fun findByName(name: String): Weapon?
}