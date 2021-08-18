package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.setting.SettingObject

interface FeatureContainerService {
  val objectClass: Class<*>

  fun containerMeta(obj: FeatureContainerData): FeatureContainer
}