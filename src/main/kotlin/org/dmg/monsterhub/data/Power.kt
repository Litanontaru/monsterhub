package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.service.FreeFeatureDataProvider
import org.dmg.monsterhub.service.PowerEffectDataProvider.Companion.POWER_EFFECT
import javax.persistence.Entity

@Entity
class Power : ContainerData(), SkillLike {
  override var skillType: SkillType = SkillType.OFFENSE

  private fun calculator(): PowerRateCalculator = features
      .find { it.feature.featureType == POWER_EFFECT }
      ?.let { it.feature as PowerEffect }
      ?.let { it.calculator }
      ?: PowerRateCalculator.STANDARD

  override fun meta(): List<FeatureContainerItem> = META

  override fun rate() = calculator().calculator(this)

  companion object {
    const val POWER = "POWER"

    val META = listOf(
        FeatureContainerItem().apply {
          name = "Эффект"
          featureType = POWER_EFFECT
          onlyOne = true
        },

        FeatureContainerItem().apply {
          name = "Длительность эффекта"
          featureType = FreeFeatureDataProvider.EFFECT_DURATION
          onlyOne = true
        },

        FeatureContainerItem().apply {
          name = "Дистанция"
          featureType = FreeFeatureDataProvider.EFFECT_DISTANCE
          onlyOne = true
        },

        FeatureContainerItem().apply {
          name = "Область действия"
          featureType = FreeFeatureDataProvider.AREA_OF_EFFECT
          onlyOne = true
        },

        FeatureContainerItem().apply {
          name = "Тип цели"
          featureType = FreeFeatureDataProvider.EFFECT_TARGET_TYPE
        },

        FeatureContainerItem().apply {
          name = "Угрозы"
          featureType = FreeFeatureDataProvider.EFFECT_THREAT
        },


        FeatureContainerItem().apply {
          name = "Проверка"
          featureType = FreeFeatureDataProvider.ACTIVATION_ROLL
          onlyOne = true
        },

        FeatureContainerItem().apply {
          name = "Событие"
          featureType = FreeFeatureDataProvider.ACTIVATION_EVENT
        },

        FeatureContainerItem().apply {
          name = "Длительность активации"
          featureType = FreeFeatureDataProvider.ACTIVATION_DURATION
          onlyOne = true
        },

        FeatureContainerItem().apply {
          name = "Плата"
          featureType = FreeFeatureDataProvider.ACTIVATION_PAYMENT
        },

        FeatureContainerItem().apply {
          name = "Условия"
          featureType = FreeFeatureDataProvider.POWER_CONDITION
        },

        FeatureContainerItem().apply {
          name = "Резерв"
          featureType = FreeFeatureDataProvider.POWER_RESERVE
        }
    )
  }
}