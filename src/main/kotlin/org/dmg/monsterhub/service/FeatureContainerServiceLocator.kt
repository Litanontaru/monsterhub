package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.springframework.stereotype.Service

@Service
class FeatureContainerServiceLocator(
    private val services: List<FeatureContainerService>
) {
  fun containerMeta(obj: FeatureContainerData): FeatureContainer? =
      services
          .find { obj::class.java.isAssignableFrom(it.objectClass) }
          ?.containerMeta(obj)
}