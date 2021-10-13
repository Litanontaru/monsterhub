package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.data.Power.Companion.POWER
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.PowerRepository
import org.springframework.stereotype.Service

@Service
class PowerDataProvider(
    repository: PowerRepository
) : SimpleSettingObjectDataProvider<Power>(POWER, "Сила", repository) {
  override fun create(): SettingObject = Power().apply { featureType = type }
}