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
  fun exists(setting: Setting, folder: String): Boolean =
      featureRepository.existsFeatureBySettingAndFolderStartingWith(setting, folder)

  fun folders(setting: Setting, folder: String) =
      featureRepository.foldersBySettingAndFolderStartingWithAndHiddenFalse(setting, folder)

  fun count(setting: Setting, folder: String) =
      featureRepository.countFeatureBySettingAndFolderAndHiddenFalse(setting, folder)

  fun features(setting: Setting, folder: String) =
      featureRepository.featureBySettingAndFolder(setting, folder)

  fun hide(id: Long) {
    featureRepository.hide(id)
  }
}