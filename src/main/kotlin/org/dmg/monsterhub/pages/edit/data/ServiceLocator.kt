package org.dmg.monsterhub.pages.edit.data

import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.pages.edit.form.EditPanelConfig
import org.dmg.monsterhub.repository.*
import org.dmg.monsterhub.service.ObjectManagerService
import org.dmg.monsterhub.service.TransactionService

data class ServiceLocator(
    val setting: Setting,

    val source: ObjectFinderDataProviderService,
    val objectManagerService: ObjectManagerService,
    val featureDataRepository: FeatureDataRepository,
    val featureContainerItemRepository: FeatureContainerItemRepository,
    val featureDataDesignationRepository: FeatureDataDesignationRepository,
    val powerEffectRepository: PowerEffectRepository,
    val weaponRepository: WeaponRepository,
    val weaponAttackRepository: WeaponAttackRepository,
    val settingRepository: SettingRepository,
    val transactionService: TransactionService,

    val config: EditPanelConfig
) {
  lateinit var settings: List<Setting>

  val data: ObjectFinderDataProviderForSetting
    get() = source(settings)

  fun refreshSettings() {
    settings = getRecursive(setting).toList()
  }

  companion object {
    fun getRecursive(setting: Setting): Sequence<Setting> =
        sequenceOf(setting) + setting.base.asSequence().flatMap { getRecursive(it) }
  }
}