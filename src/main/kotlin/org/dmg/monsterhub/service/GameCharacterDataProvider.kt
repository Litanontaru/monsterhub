package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.GameCharacter
import org.dmg.monsterhub.data.GameCharacter.Companion.CHARACTER
import org.dmg.monsterhub.data.meta.FreeFeatureType
import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.pages.SettingObjectTreeFilter
import org.dmg.monsterhub.repository.GameCharacterRepository
import org.springframework.stereotype.Service

@Service
class GameCharacterDataProvider(
  dependencyAnalyzer: DependencyAnalyzer,
  repository: GameCharacterRepository
) : SimpleSettingObjectDataProvider<GameCharacter>(CHARACTER, dependencyAnalyzer, repository) {
  override fun factories(): List<SettingObjectFactory> = listOf(SettingObjectFactory(CHARACTER, "Персонаж") {
    FreeFeatureType().apply { featureType = CHARACTER }
  })

  override fun countChildrenAlikeBySetting(parent: Folder?, filter: SettingObjectTreeFilter, setting: Setting) = 0
}