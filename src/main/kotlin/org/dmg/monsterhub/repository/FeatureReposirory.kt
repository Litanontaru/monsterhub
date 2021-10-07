package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.pages.SettingObjectTreeNode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface FeatureRepository : JpaRepository<Feature, Long> {
  fun existsFeatureBySettingAndFolderStartingWith(setting: Setting, folder: String): Boolean

  @Query("SELECT DISTINCT f.folder FROM Feature f WHERE f.setting = :setting AND f.folder LIKE :folder AND f.hidden = false")
  fun foldersBySettingAndFolderStartingWithAndHiddenFalse(setting: Setting, folder: String): List<String>

  fun countFeatureBySettingAndFolderAndHiddenFalse(setting: Setting, folder: String): Int

  @Query("SELECT f.id as id, f.name as name, f.featureType as featureType, f.folder as folder FROM Feature f WHERE f.setting = :setting AND f.folder = :folder")
  fun featureBySettingAndFolder(setting: Setting, folder: String): List<SettingObjectTreeNode>

  @Modifying
  @Query("UPDATE Feature f SET f.hidden = true WHERE f.id = :id")
  fun hide(id: Long)

  @Modifying
  @Query("UPDATE Feature f SET f.folder = :newFolder WHERE f.id = :id")
  fun move(id: Long, newFolder: String)
}