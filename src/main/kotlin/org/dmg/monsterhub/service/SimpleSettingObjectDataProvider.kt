package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.SettingObjectRepository

abstract class SimpleSettingObjectDataProvider<T : SettingObject>(
    objectClass: Class<*>,
    val type: String,
    override val name: String,
    repository: SettingObjectRepository<T>
) : AbstractSettingObjectDataProvider<T>(objectClass, repository) {

  override fun supportType(type: String) = type == this.type
}