package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.meta.FeatureContainer

interface FeatureContainerService {
  val objectClass: Class<*>

  fun containerMeta(obj: FeatureContainerData): FeatureContainer
}