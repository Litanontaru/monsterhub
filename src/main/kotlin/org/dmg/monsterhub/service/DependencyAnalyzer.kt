package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DependencyAnalyzer(
  val featureDataRepository: FeatureDataRepository,
  val containerDataRepository: ContainerDataRepository,
  val weaponAttackRepository: WeaponAttackRepository,
  val weaponRepository: WeaponRepository,
  val creatureRepository: CreatureRepository
) {
  @Transactional
  fun analyzeMoveToSetting(obj: SettingObject, destination: Setting): List<SettingObject> =
    getSettingObjectDependencies(obj)
      .filter { dependent -> !ServiceLocator.getRecursive(dependent.setting).contains(destination) }

  @Transactional
  fun getSettingObjectDependencies(obj: SettingObject) =
    when (obj) {
      is Feature -> getFeatureDependencies(obj, setOf())
      else -> listOf()
    }
      .distinct()
      .filter { it != obj }


  private fun getFeatureDependencies(feature: Feature, stack: Set<Any>): List<SettingObject> {
    if (stack.contains(feature)) {
      return listOf()
    }
    val newStack = stack + setOf(feature)

    return listOf(feature) +
        getContainerDependencies(featureDataRepository.findAllByFeature(feature.id), newStack) +
        getCreatureDependencies(feature, newStack)
  }

  private fun getContainerDependencies(containers: List<FeatureDataContainer>, stack: Set<Any>): List<SettingObject> =
    containers
      .flatMap {
        when {
          it.getFeatureContainerId() != null -> getFeatureContainerDataDependencies(it.getFeatureContainerId()!!, stack)
          it.getMainFeatureId() != null -> getMainFeatureDataId(it.getMainFeatureId()!!, stack)
          it.getWeaponAttackId() != null -> getWeaponAttackDependencies(it.getWeaponAttackId()!!, stack)

          else -> listOf()
        }
      }

  private fun getFeatureContainerDataDependencies(id: Long, stack: Set<Any>) =
    containerDataRepository.findById(id).orElse(null)
      ?.let { getFeatureDependencies(it, stack) }
      ?: listOf()

  private fun getMainFeatureDataId(id: Long, stack: Set<Any>) =
    featureDataRepository.findById(id).orElse(null)
      ?.let { featureDataRepository.findAllByFeatureData(it.id) }
      ?.let { getContainerDependencies(it, stack) }
      ?: listOf()

  private fun getWeaponAttackDependencies(weaponAttackId: Long, stack: Set<Any>) =
    weaponAttackRepository.getWeaponIdByAttackId(weaponAttackId)
      .let { weaponRepository.findById(it).orElse(null) }
      ?.let { getFeatureDependencies(it, stack) }
      ?: listOf()

  private fun getCreatureDependencies(feature: Feature, stack: Set<Any>) = when (feature) {
    is Creature -> {
      creatureRepository.getSubCreatures(feature.id)
        .flatMap { getFeatureContainerDataDependencies(it, stack) }
    }
    else -> listOf()
  }

  //--------------------------------------------------------------------------------------------------------------------

  fun findUsages(featureId: Long): List<Long> {
    val result = mutableSetOf<Long>()
    findNextUsagesRecursive(featureId, result)
    return result.toList()
  }

  private fun findNextUsagesRecursive(featureId: Long, result: MutableSet<Long>) {
    findNextUsages(featureId)
      .filter { result.add(it) }
      .forEach { findNextUsagesRecursive(it, result) }
  }

  private fun findNextUsages(featureId: Long): List<Long> = featureDataRepository
    .findAllByFeature(featureId)
    .let { findNextUsages(it) }
    .let { it + creatureRepository.getSubCreatures(featureId) }
    .distinct()

  private fun findNextToFeatureData(featureDataId: Long): List<Long> =
    findNextUsages(featureDataRepository.findAllByFeatureData(featureDataId)).distinct()

  private fun findNextUsages(containers: List<FeatureDataContainer>): List<Long> =
    containers.mapNotNull { it.getMainFeatureId() }.flatMap { findNextToFeatureData(it) } +
        containers.mapNotNull { it.getFeatureContainerId() } +
        containers.mapNotNull { it.getWeaponAttackId() }.map { weaponAttackRepository.getWeaponIdByAttackId(it) }
}