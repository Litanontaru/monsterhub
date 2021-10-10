package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.meta.FreeFeatureType
import org.dmg.monsterhub.data.meta.FreeFeatureType.Companion.FREE_FEATURE_TYPE
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.FreeFeatureTypeRepository
import org.springframework.stereotype.Service

@Service
class FreeFeatureTypeDataProvider(
    repository: FreeFeatureTypeRepository
) : SimpleSettingObjectDataProvider<FreeFeatureType>(FreeFeatureType::class.java, FREE_FEATURE_TYPE, "Тип свободного аспекта", repository) {
  override fun create(): SettingObject = FreeFeatureType()
}