package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.WeaponAttack
import org.springframework.data.jpa.repository.Query

interface WeaponAttackRepository : DBObjectRepository<WeaponAttack> {
  @Query(value = "SELECT weapon_id FROM weapon_attack WHERE id = :weaponAttackId", nativeQuery = true)
  fun getWeaponIdByAttackId(weaponAttackId: Long): Long
}