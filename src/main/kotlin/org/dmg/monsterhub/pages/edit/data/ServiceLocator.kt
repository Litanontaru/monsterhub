package org.dmg.monsterhub.pages.edit.data

import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.pages.ObjectTreeDataProvider
import org.dmg.monsterhub.pages.edit.form.EditPanelConfig
import org.dmg.monsterhub.repository.*
import org.dmg.monsterhub.service.FeatureDataRepository
import org.dmg.monsterhub.service.TransactionService

data class ServiceLocator(
    val settigs: List<Setting>,

    val data: ObjectTreeDataProvider,
    val finderData: ObjectFinderDataProviderForSetting,
    val featureDataRepository: FeatureDataRepository,
    val featureContainerItemRepository: FeatureContainerItemRepository,
    val featureDataDesignationRepository: FeatureDataDesignationRepository,
    val weaponRepository: WeaponRepository,
    val weaponAttackRepository: WeaponAttackRepository,
    val settingRepository: SettingRepository,

    val transactionService: TransactionService,

    val config: EditPanelConfig
)