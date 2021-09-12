package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.FreeFeature
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.FreeFeatureRepository
import org.springframework.stereotype.Service

@Service
class FreeFeatureDataProvider(
    override val repository: FreeFeatureRepository
) : AbstractSettingObjectDataProvider<FreeFeature>(FreeFeature::class.java, repository) {

  override fun supportType(type: String) = MY_TYPES.contains(type)

  override val name: String = "Свободный аспект"

  override fun create(): SettingObject = FreeFeature().apply { featureType = "NONE" }

  companion object {
    const val EFFECT_DURATION = "EFFECT_DURATION"
    const val EFFECT_DISTANCE = "EFFECT_DISTANCE"
    const val AREA_OF_EFFECT = "AREA_OF_EFFECT"
    const val EFFECT_TARGET_TYPE = "EFFECT_TARGET_TYPE"
    const val EFFECT_THREAT = "EFFECT_THREAT"

    const val ACTIVATION_ROLL = "ACTIVATION_ROLL"
    const val ACTIVATION_EVENT = "ACTIVATION_EVENT"
    const val ACTIVATION_DURATION = "ACTIVATION_DURATION"
    const val ACTIVATION_PAYMENT = "ACTIVATION_PAYMENT"
    const val POWER_CONDITION = "POWER_CONDITION"
    const val POWER_RESERVE = "POWER_RESERVE"


    val MY_TYPES = setOf(
        "NONE",

        "POISON_EFFECT",
        "POISON_DELAY",
        "POISONING_WAY",

        "DISEASE",

        EFFECT_DURATION,
        EFFECT_DISTANCE,
        AREA_OF_EFFECT,
        EFFECT_TARGET_TYPE,
        EFFECT_THREAT,

        ACTIVATION_ROLL,
        ACTIVATION_EVENT,
        ACTIVATION_DURATION,
        ACTIVATION_PAYMENT,
        POWER_CONDITION,
        POWER_RESERVE,

        "COUNTER_SIZE_TABLE",

        "AREA_SPHERE",
        "AREA_CUBE",
        "AREA_EXPLOSION",
        "AREA_LINE",
        "AREA_CONE",

        "COLLISION",

        "WEAPON_FEATURE",
        "WEAPON_ATTACK_FEATURE",
        "POWER_UP_TYPE",

        "ARMOR_FEATURE"
    )
  }
}