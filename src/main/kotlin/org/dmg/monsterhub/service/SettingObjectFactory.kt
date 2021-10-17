package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.SettingObject

data class SettingObjectFactory(
    val featureType: String,
    val name: String,
    val create: () -> SettingObject
)