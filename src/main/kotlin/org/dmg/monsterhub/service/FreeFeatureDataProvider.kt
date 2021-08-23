package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.FreeFeature
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.FreeFeatureRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class FreeFeatureDataProvider(
    val repository: FreeFeatureRepository
) : SettingObjectDataProvider {

  override val objectClass: Class<*> = FreeFeature::class.java

  override fun supportType(type: String) = MY_TYPES.contains(type)

  override val name: String = "Свободный аспект"

  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySetting(setting)

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByFeatureTypeAndNameContainingAndSettingIn(type, name, settings, pageable)

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>) =
      repository.countByFeatureTypeAndNameContainingAndSettingIn(type, name, settings)

  override fun refresh(one: SettingObject) = repository.getById(one.id)

  override fun save(one: SettingObject) {
    repository.save(one as FreeFeature)
  }

  override fun delete(one: SettingObject) {
    repository.delete(one as FreeFeature)
  }

  override fun create(): SettingObject = FreeFeature().apply { featureType = "NONE" }

  companion object {
    val MY_TYPES = setOf(
        "NONE",

        "POISON_EFFECT",
        "POISON_DELAY",
        "POISONING_WAY",

        "DISEASE",

        "POWER_EFFECT",

        "ACTIVATION_ROLL",
        "ACTIVATION_EVENT",
        "ACTIVATION_DURATION",
        "ACTIVATION_PAYMENT",
        "POWER_CONDITION",
        "POWER_RESERVE",
        "EFFECT_DURATION",
        "EFFECT_DISTANCE",
        "AREA_OF_EFFECT",
        "EFFECT_TARGET_TYPE",
        "EFFECT_THREAT"
    )
  }
}