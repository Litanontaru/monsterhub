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

    fun getNaturalWeapons(keys: Sequence<String>): List<Weapon> = repository.findNaturalWeapon(keys.toList())

    fun isNatural(weapon: Weapon) = weapon.features.find { it.feature == "Естественное оружие" } != null
}