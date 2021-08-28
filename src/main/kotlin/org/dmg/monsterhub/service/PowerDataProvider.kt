package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.PowerRepository
import org.dmg.monsterhub.service.FreeFeatureDataProvider.Companion.ACTIVATION_DURATION
import org.dmg.monsterhub.service.FreeFeatureDataProvider.Companion.ACTIVATION_EVENT
import org.dmg.monsterhub.service.FreeFeatureDataProvider.Companion.ACTIVATION_PAYMENT
import org.dmg.monsterhub.service.FreeFeatureDataProvider.Companion.ACTIVATION_ROLL
import org.dmg.monsterhub.service.FreeFeatureDataProvider.Companion.AREA_OF_EFFECT
import org.dmg.monsterhub.service.FreeFeatureDataProvider.Companion.EFFECT_DISTANCE
import org.dmg.monsterhub.service.FreeFeatureDataProvider.Companion.EFFECT_DURATION
import org.dmg.monsterhub.service.FreeFeatureDataProvider.Companion.EFFECT_TARGET_TYPE
import org.dmg.monsterhub.service.FreeFeatureDataProvider.Companion.EFFECT_THREAT
import org.dmg.monsterhub.service.FreeFeatureDataProvider.Companion.POWER_CONDITION
import org.dmg.monsterhub.service.FreeFeatureDataProvider.Companion.POWER_RESERVE
import org.dmg.monsterhub.service.PowerEffectDataProvider.Companion.POWER_EFFECT
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PowerDataProvider(
    val repository: PowerRepository
) : SimpleSettingObjectDataProvider(), FeatureContainerService {
  override val objectClass: Class<*> = Power::class.java

  override val type: String = "POWER"

  override val name: String = "Сила"

  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySetting(setting)

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByNameContainingAndSettingIn(name, settings, pageable)

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>) =
      repository.countByNameContainingAndSettingIn(name, settings)

  override fun refresh(one: SettingObject) = repository.getById(one.id)

  override fun save(one: SettingObject) {
    repository.save(one as Power)
  }

  override fun delete(one: SettingObject) {
    repository.delete(one as Power)
  }

  override fun create(): SettingObject = Power().apply { featureType = type }

  override fun containerMeta(obj: FeatureContainerData): FeatureContainer = Meta

  private object Meta : FeatureContainer {
    override val containFeatureTypes: MutableList<FeatureContainerItem> = mutableListOf(
        FeatureContainerItem().apply {
          name = "Эффект"
          featureType = POWER_EFFECT
          onlyOne = true
        },

        FeatureContainerItem().apply {
          name = "Длительность эффекта"
          featureType = EFFECT_DURATION
          onlyOne = true
        },

        FeatureContainerItem().apply {
          name = "Дистанция"
          featureType = EFFECT_DISTANCE
          onlyOne = true
        },

        FeatureContainerItem().apply {
          name = "Область действия"
          featureType = AREA_OF_EFFECT
          onlyOne = true
        },

        FeatureContainerItem().apply {
          name = "Тип цели"
          featureType = EFFECT_TARGET_TYPE
        },

        FeatureContainerItem().apply {
          name = "Угрозы"
          featureType = EFFECT_THREAT
        },


        FeatureContainerItem().apply {
          name = "Проверка"
          featureType = ACTIVATION_ROLL
          onlyOne = true
        },

        FeatureContainerItem().apply {
          name = "Событие"
          featureType = ACTIVATION_EVENT
        },

        FeatureContainerItem().apply {
          name = "Длительность активации"
          featureType = ACTIVATION_DURATION
          onlyOne = true
        },

        FeatureContainerItem().apply {
          name = "Плата"
          featureType = ACTIVATION_PAYMENT
        },

        FeatureContainerItem().apply {
          name = "Условия"
          featureType = POWER_CONDITION
        },

        FeatureContainerItem().apply {
          name = "Резерв"
          featureType = POWER_RESERVE
        }
    )
  }
}