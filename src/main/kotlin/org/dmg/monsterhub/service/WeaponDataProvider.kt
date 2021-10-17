package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Weapon
import org.dmg.monsterhub.data.Weapon.Companion.WEAPON
import org.dmg.monsterhub.repository.WeaponRepository
import org.springframework.stereotype.Service

@Service
class WeaponDataProvider(
    repository: WeaponRepository
) : SimpleSettingObjectDataProvider<Weapon>(WEAPON, repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory(WEAPON, "Оружие") {
    Weapon().apply { featureType = WEAPON }
  })
}