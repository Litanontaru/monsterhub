package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.setting.Setting
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface FeatureRepository : JpaRepository<Feature, Long> {
  fun existsFeatureBySettingAndFolderStartingWith(setting: Setting, folder: String): Boolean

  fun existsFeatureBySettingAndFolderStartingWithAndNameContaining(setting: Setting, folder: String, name: String): Boolean

  @Query("SELECT DISTINCT f.folder FROM Feature f WHERE f.setting = :setting AND f.folder LIKE :folder AND f.hidden = false")
  fun foldersBySettingAndFolderStartingWithAndHiddenFalse(setting: Setting, folder: String): List<String>

  @Query("SELECT DISTINCT f.folder FROM Feature f WHERE f.setting = :setting AND f.folder LIKE :folder AND f.name LIKE :name AND f.hidden = false")
  fun foldersBySettingAndFolderStartingWithAndNameContainingAndHiddenFalse(setting: Setting, folder: String, name: String): List<String>

  fun countFeatureBySettingAndFolderAndHiddenFalse(setting: Setting, folder: String): Int

  fun countFeatureBySettingAndFolderAndNameContainingAndHiddenFalse(setting: Setting, folder: String, name: String): Int

  @Modifying
  @Query("UPDATE Feature f SET f.hidden = true WHERE f.id = :id")
  fun hide(id: Long)

  @Modifying
  @Query("UPDATE Feature f SET f.folder = :newFolder WHERE f.id = :id")
  fun move(id: Long, newFolder: String)

  @Modifying
  @Query("UPDATE Feature f SET f.setting = :setting WHERE f.id = :id")
  fun move(id: Long, setting: Setting)
}