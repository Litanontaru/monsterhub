package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.GameCharacter
import org.dmg.monsterhub.data.GameCharacter.Companion.CHARACTER
import org.dmg.monsterhub.data.meta.FreeFeatureType
import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.repository.GameCharacterRepository
import org.springframework.stereotype.Service

@Service
class GameCharacterDataProvider(
    repository: GameCharacterRepository
) : SimpleSettingObjectDataProvider<GameCharacter>(CHARACTER, repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory(CHARACTER, "Персонаж") {
    FreeFeatureType().apply { featureType = CHARACTER }
  })

  override fun countChildrenAlikeBySetting(parent: Folder?, search: String, setting: Setting) = 0
}