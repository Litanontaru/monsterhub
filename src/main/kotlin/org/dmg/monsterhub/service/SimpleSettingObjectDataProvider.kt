package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.SettingObjectRepository

abstract class SimpleSettingObjectDataProvider<T : SettingObject>(
    repository: SettingObjectRepository<T>
) : AbstractSettingObjectDataProvider<T>(repository) {
  abstract val type: String

  override fun supportType(type: String) = type == this.type
}