package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Skill
import org.dmg.monsterhub.data.Skill.Companion.SKILL
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.SkillRepository
import org.springframework.stereotype.Service

@Service
class SkillDataProvider(
    repository: SkillRepository
) : SimpleSettingObjectDataProvider<Skill>(SKILL, "Способность", repository) {
  override fun create(): SettingObject = Skill().apply { featureType = SKILL }
}