package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Skill
import org.dmg.monsterhub.data.Skill.Companion.SKILL
import org.dmg.monsterhub.repository.SkillRepository
import org.springframework.stereotype.Service

@Service
class SkillDataProvider(
    repository: SkillRepository
) : SimpleSettingObjectDataProvider<Skill>(SKILL, repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory("Способность") {
    Skill().apply { featureType = SKILL }
  })
}