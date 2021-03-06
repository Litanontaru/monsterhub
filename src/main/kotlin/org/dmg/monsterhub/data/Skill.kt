package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.Feature
import javax.persistence.Entity

@Entity
class Skill: Feature(), SkillLike {
  override var skillType: SkillType = SkillType.OFFENSE

  companion object {
    val SKILL = "SKILL"
  }
}