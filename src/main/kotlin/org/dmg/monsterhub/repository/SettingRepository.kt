package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.jpa.repository.JpaRepository

interface SettingRepository : JpaRepository<Setting, Long>