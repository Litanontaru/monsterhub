package org.dmg.monsterhub.pages.edit.data

import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.data.Power.Companion.POWER

object PowerService {
  fun wrapWithAcquisition(power: Power, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): Power {
    val list = locator
        .powerEffectRepository.findAllByNameAndSettingIn("Приобретение", locator.settings)
    return list
        .find { true }
        ?.let { effect ->
          Power().let {
            it.featureType = POWER
            it.name = power.name
            update(it) { } as Power
          }.also { acquisitionPower ->
            //todo add other standard acquisition aspects: Больше 4-х лет, На себя, Антимагия и контрэффекты, 1 история
            acquisitionPower.features.add(
                FeatureData()
                    .let { update(it) {} as FeatureData }
                    .also { nestedFeatureData ->
                      nestedFeatureData.feature = effect
                      nestedFeatureData.features.add(
                          FeatureData()
                              .let { update(it) {} as FeatureData }
                              .also {
                                it.feature = power
                              }
                              .let { update(it) {} as FeatureData }
                      )
                    }
                    .let { update(it) {} as FeatureData }
            )
          }.let {
            update(it) { } as Power
          }
        }
        ?: power
  }
}