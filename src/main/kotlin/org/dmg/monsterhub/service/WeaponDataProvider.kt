package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Weapon
import org.dmg.monsterhub.data.Weapon.Companion.WEAPON
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.WeaponRepository
import org.springframework.stereotype.Service

@Service
class WeaponDataProvider(
    repository: WeaponRepository
) : SimpleSettingObjectDataProvider<Weapon>(Weapon::class.java, WEAPON, "Оружие", repository) {
  override fun create(): SettingObject = Weapon().apply { featureType = WEAPON }
}