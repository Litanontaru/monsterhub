package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.repository.FeatureRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class FeatureService(
    val featureRepository: FeatureRepository
) {
  fun exists(setting: Setting, folder: String, filter: String): Boolean = when {
    filter.isBlank() -> featureRepository.existsFeatureBySettingAndFolderStartingWith(setting, folder)
    else -> featureRepository.existsFeatureBySettingAndFolderStartingWithAndNameContaining(setting, folder, filter)
  }

  fun folders(setting: Setting, folder: String, filter: String) = when {
    filter.isBlank() -> featureRepository.foldersBySettingAndFolderStartingWithAndHiddenFalse(setting, folder)
    else -> featureRepository.foldersBySettingAndFolderStartingWithAndNameContainingAndHiddenFalse(setting, folder, "%$filter%")
  }

  fun count(setting: Setting, folder: String, filter: String) = when {
    filter.isBlank() -> featureRepository.countFeatureBySettingAndFolderAndHiddenFalse(setting, folder)
    else -> featureRepository.countFeatureBySettingAndFolderAndNameContainingAndHiddenFalse(setting, folder, filter)
  }

  fun features(setting: Setting, folder: String, filter: String) = when {
    filter.isBlank() -> featureRepository.featureBySettingAndFolder(setting, folder)
    else -> featureRepository.featureBySettingAndFolder(setting, folder, filter)
  }

  fun hide(id: Long) {
    featureRepository.hide(id)
  }

  fun move(id: Long, newFolder: String) {
    featureRepository.move(id, newFolder)
  }

  fun move(id: Long, setting: Setting) {
    featureRepository.move(id, setting)
  }
}