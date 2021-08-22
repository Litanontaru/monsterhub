package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Skill
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.SkillRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SkillDataProvider(
    val repository: SkillRepository
) : SimpleSettingObjectDataProvider() {
  override val objectClass: Class<*> = Skill::class.java

  override val type: String = "SKILL"

  override val name: String = "Способность"

  override fun getAllBySetting(setting: Setting): List<SettingObject> = repository.findAllBySetting(setting)

  override fun getAlikeBySettings(type: String, name: String, settings: List<Setting>, pageable: Pageable) =
      repository.findAllByNameContainingAndSettingIn(name, settings, pageable)

  override fun countAlikeBySettings(type: String, name: String, settings: List<Setting>) =
      repository.countByNameContainingAndSettingIn(name, settings)

  override fun refresh(one: SettingObject) = repository.getById(one.id)

  override fun save(one: SettingObject) {
    repository.save(one as Skill)
  }

  override fun delete(one: SettingObject) {
    repository.delete(one as Skill)
  }

  override fun create(): SettingObject = Skill().apply { featureType = type }
}