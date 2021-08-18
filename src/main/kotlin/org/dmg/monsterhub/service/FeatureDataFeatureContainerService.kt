package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.FeatureData
import org.springframework.stereotype.Service

@Service
class FeatureDataFeatureContainerService : FeatureContainerService {
  override val objectClass: Class<*> = FeatureData::class.java

  override fun containerMeta(obj: FeatureContainerData) = (obj as FeatureData).feature
}