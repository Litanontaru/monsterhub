package org.dmg.monsterhub.pages.edit.data

import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.pages.ObjectTreeDataProvider
import org.dmg.monsterhub.pages.edit.form.EditPanelConfig
import org.dmg.monsterhub.repository.FeatureContainerItemRepository
import org.dmg.monsterhub.repository.FeatureDataDesignationRepository
import org.dmg.monsterhub.repository.WeaponAttackRepository
import org.dmg.monsterhub.repository.WeaponRepository
import org.dmg.monsterhub.service.FeatureDataRepository

data class ServiceLocator(
    val settigs: List<Setting>,

    val data: ObjectTreeDataProvider,
    val finderData: ObjectFinderDataProviderForSetting,
    val featureDataRepository: FeatureDataRepository,
    val featureContainerItemRepository: FeatureContainerItemRepository,
    val featureDataDesignationRepository: FeatureDataDesignationRepository,
    val weaponRepository: WeaponRepository,
    val weaponAttackRepository: WeaponAttackRepository,

    val config: EditPanelConfig
)