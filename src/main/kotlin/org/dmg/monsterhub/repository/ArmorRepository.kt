package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.Armor
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.stereotype.Repository

@Repository
interface ArmorRepository : SettingObjectRepository<Armor>