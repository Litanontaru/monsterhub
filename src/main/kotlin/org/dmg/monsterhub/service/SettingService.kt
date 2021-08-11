package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.repository.SettingRepository
import org.springframework.stereotype.Service

@Service
class SettingService(
        val repository: SettingRepository
) {
    fun get(id: Long) = repository.getById(id)

    fun save(setting: Setting) {
        repository.save(setting)
    }
}