package org.dmg.monsterhub.model

import org.springframework.stereotype.Service

@Service
class WeaponService(
    val repository: OldWeaponRepository
) {
  fun save(weapon: OldWeapon) {
    repository.save(weapon)
  }

  fun find(name: String): OldWeapon? = repository.findByName(name)

  fun getNaturalWeapons(keys: Sequence<String>): List<OldWeapon> = repository
      .findNaturalWeapon(keys.toList())
      .distinctBy { it.id }

  fun isNatural(weapon: OldWeapon) = weapon.features.find { it.feature == "Естественное оружие" } != null
}