package org.dmg.monsterhub.pages

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider
import com.vaadin.flow.data.provider.Query
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.service.SettingObjectDataProvider
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.stream.Stream

@Service
class ObjectFinderDataProviderService(
    private val dataProviders: List<SettingObjectDataProvider>
) {
  operator fun invoke(setting: Setting) = ObjectFinderDataProviderForSetting(dataProviders, setting)
}

class ObjectFinderDataProviderForSetting(
    private val dataProviders: List<SettingObjectDataProvider>,
    private val setting: Setting
) {
  private fun getRecursive(setting: Setting): Sequence<Setting> =
      sequenceOf(setting) + setting.base.asSequence().flatMap { getRecursive(it) }

  operator fun invoke(type: String): ObjectFinderDataProvider =
      ObjectFinderDataProvider(
          dataProviders.find { it.supportType(type) }!!,
          getRecursive(setting).toList()
      )
}

class ObjectFinderDataProvider(
    private val dataProvider: SettingObjectDataProvider,
    private val settings: List<Setting>
) : AbstractBackEndDataProvider<SettingObject, String>() {
  override fun sizeInBackEnd(query: Query<SettingObject, String>?) =
      query
          ?.filter
          ?.orElse(null)
          ?.let {
            query.page
            query.pageSize
            dataProvider.countAlikeBySettings(it, settings)
          }
          ?: 0

  override fun fetchFromBackEnd(query: Query<SettingObject, String>?) =
      query
          ?.filter
          ?.orElse(null)
          ?.let {
            dataProvider
                .getAlikeBySettings(it, settings, PageRequest.of(query.page, query.pageSize))
                .stream()
          }
          ?: Stream.empty<SettingObject>()
}