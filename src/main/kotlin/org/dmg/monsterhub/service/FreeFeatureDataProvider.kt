package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.FreeFeature
import org.dmg.monsterhub.data.meta.FreeFeatureType
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.repository.FreeFeatureRepository
import org.dmg.monsterhub.repository.FreeFeatureTypeRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class FreeFeatureDataProvider(
    override val repository: FreeFeatureRepository,
    private val typeRepository: FreeFeatureTypeRepository
) : AbstractSettingObjectDataProvider<FreeFeature>(repository) {

  lateinit var supportedTypes: List<FreeFeatureType>
  private lateinit var supportedTypeNames: Set<String>

  init {
    refreshTypes()
  }

  final fun refreshTypes() {
    supportedTypes = typeRepository.findAll().sortedBy { it.display }
    supportedTypeNames = typeRepository.findAll().map { it.name }.toSet()
  }

  override fun getBySettings(type: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByFeatureTypeAndSettingIn(type, settings, pageable)

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByFeatureTypeAndNameContainingAndSettingIn(type, name, settings, pageable)

  override fun countBySettings(type: String, settings: List<Setting>) =
      repository.countByFeatureTypeAndSettingIn(type, settings)

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>) =
      repository.countByFeatureTypeAndNameContainingAndSettingIn(type, name, settings)

  override fun supportType(type: String) = supportedTypeNames.contains(type)

  override fun factories(): List<SettingObjectFactory> = supportedTypes.map {
    SettingObjectFactory(it.name, it.display) { FreeFeature().apply { featureType = it.name } }
  }

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
  }
}