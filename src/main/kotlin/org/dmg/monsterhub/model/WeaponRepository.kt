package org.dmg.monsterhub.model

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface WeaponRepository : CrudRepository<Weapon, Long> {
  fun findByName(name: String): Weapon?

  @Query("SELECT wf.weapon FROM WeaponFeature wf WHERE wf.feature = 'Естественное оружие' and wf.details in :keys")
  fun findNaturalWeapon(keys: List<String>): List<Weapon>
}