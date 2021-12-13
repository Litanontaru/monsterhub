package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.FeatureData
import org.springframework.data.jpa.repository.Query

interface FeatureDataRepository : DBObjectRepository<FeatureData> {
  @Query(
      value = "SELECT main_feature_id AS mainFeatureId, feature_container_id AS featureContainerId, weapon_attack_id AS weaponAttackId FROM feature_data WHERE feature_id = :featureId",
      nativeQuery = true
  )
  fun findAllByFeature(featureId: Long): List<FeatureDataContainer>

  @Query(
      value = "SELECT main_feature_id AS mainFeatureId, feature_container_id AS featureContainerId, weapon_attack_id AS weaponAttackId FROM feature_data WHERE id = :featureDataId",
      nativeQuery = true
  )
  fun findAllByFeatureData(featureDataId: Long): List<FeatureDataContainer>

  fun findAllByContainerData_IdAndFeature_FeatureType(containerData_id: Long, feature_featureType: String): List<FeatureData>

  fun findAllByMainFeature_IdAndFeature_FeatureType(mainFeature_id: Long, feature_featureType: String): List<FeatureData>
}

interface FeatureDataContainer {
  fun getMainFeatureId(): Long?
  fun getFeatureContainerId(): Long?
  fun getWeaponAttackId(): Long?
}