package org.dmg.monsterhub.model

import org.springframework.stereotype.Service

@Service
class WeaponService(
        val repository: WeaponRepository
) {
    fun save(weapon: Weapon) {
        repository.save(weapon)
    }

    fun find(name: String): Weapon? = repository.findByName(name)
}