package org.dmg.monsterhub.pages.edit.data

import org.dmg.monsterhub.pages.ObjectTreeDataProvider
import org.dmg.monsterhub.pages.edit.data.ObjectFinderDataProviderForSetting
import org.dmg.monsterhub.repository.FeatureContainerItemRepository
import org.dmg.monsterhub.repository.FeatureDataDesignationRepository
import org.dmg.monsterhub.service.FeatureContainerServiceLocator
import org.dmg.monsterhub.service.FeatureDataRepository

data class ServiceLocator(
    val data: ObjectTreeDataProvider,
    val fiderData: ObjectFinderDataProviderForSetting,
    val featureDataRepository: FeatureDataRepository,
    val featureContainerItemRepository: FeatureContainerItemRepository,
    val featureDataDesignationRepository: FeatureDataDesignationRepository,
    val featureContainerServiceLocator: FeatureContainerServiceLocator) {
}