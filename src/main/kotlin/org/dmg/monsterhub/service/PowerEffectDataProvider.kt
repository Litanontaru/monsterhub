package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.PowerEffect
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.PowerEffectRepository
import org.springframework.stereotype.Service

@Service
class PowerEffectDataProvider(
    repository: PowerEffectRepository
) : SimpleSettingObjectDataProvider<PowerEffect>(POWER_EFFECT, "Эффект Силы", repository) {
  override fun create(): SettingObject = PowerEffect().apply { featureType = POWER_EFFECT }

  companion object {
    val POWER_EFFECT = "POWER_EFFECT"
  }
}