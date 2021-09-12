package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Trait
import org.dmg.monsterhub.data.Trait.Companion.TRAIT
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.TraitRepository
import org.springframework.stereotype.Service

@Service
class TraitDataProvider(
    repository: TraitRepository
) : SimpleSettingObjectDataProvider<Trait>(repository) {
  override val objectClass: Class<*> = Trait::class.java

  override val type: String = TRAIT

  override val name: String = "Черта"

  override fun delete(one: SettingObject) {
    repository.delete(one as Trait)
  }

  override fun create(): SettingObject = Trait().apply { featureType = type }
}