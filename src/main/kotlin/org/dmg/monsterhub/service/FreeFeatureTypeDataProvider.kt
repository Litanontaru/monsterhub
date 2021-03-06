package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.meta.FreeFeatureType
import org.dmg.monsterhub.data.meta.FreeFeatureType.Companion.FREE_FEATURE_TYPE
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.FreeFeatureTypeRepository
import org.springframework.stereotype.Service

@Service
class FreeFeatureTypeDataProvider(
  dependencyAnalyzer: DependencyAnalyzer,
  repository: FreeFeatureTypeRepository,
  private val freeFeatureDataProvider: FreeFeatureDataProvider
) : SimpleSettingObjectDataProvider<FreeFeatureType>(FREE_FEATURE_TYPE, dependencyAnalyzer, repository) {
  override fun factories(): List<SettingObjectFactory> =
    listOf(SettingObjectFactory(FREE_FEATURE_TYPE, "Тип свободного аспекта") {
      FreeFeatureType().apply { featureType = FREE_FEATURE_TYPE }
    })

  override fun save(one: SettingObject): SettingObject {
    return super.save(one)
      .also { freeFeatureDataProvider.refreshTypes() }
  }
}