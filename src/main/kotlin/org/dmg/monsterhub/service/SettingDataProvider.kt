package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.Setting.Companion.SETTING
import org.dmg.monsterhub.repository.SettingRepository
import org.springframework.stereotype.Service

@Service
class SettingDataProvider(
    repository: SettingRepository
) : SimpleSettingObjectDataProvider<Setting>(SETTING, repository)