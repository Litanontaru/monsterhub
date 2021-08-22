package org.dmg.monsterhub.service

abstract class SimpleSettingObjectDataProvider: SettingObjectDataProvider {
  abstract val type: String

  override fun supportType(type: String) = type == this.type
}