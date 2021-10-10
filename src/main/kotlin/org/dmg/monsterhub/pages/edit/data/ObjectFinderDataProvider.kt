package org.dmg.monsterhub.pages.edit.data

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
  operator fun invoke(settings: List<Setting>) = ObjectFinderDataProviderForSetting(dataProviders, settings)
}

class ObjectFinderDataProviderForSetting(
    private val dataProviders: List<SettingObjectDataProvider>,
    val settings: List<Setting>
) {
  fun find(type: String): ObjectFinderDataProvider =
      ObjectFinderDataProvider(
          type,
          dataProviders.find { it.supportType(type) }!!,
          settings
      )

  fun select(type: String): ObjectSelectorDataProvider =
      ObjectSelectorDataProvider(
          type,
          dataProviders.find { it.supportType(type) }!!,
          settings
      )
}

class ObjectFinderDataProvider(
    private val type: String,
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
            when {
              it.isNotBlank() -> dataProvider.countAlikeBySettings(type, it, settings)
              else -> 0
            }
          }
          ?: 0

  override fun fetchFromBackEnd(query: Query<SettingObject, String>?) =
      query
          ?.filter
          ?.orElse(null)
          ?.let {
            when {
              it.isNotBlank() -> dataProvider
                  .getAlikeBySettings(type, it, settings, PageRequest.of(query.page, query.pageSize))
                  .stream()
              else -> Stream.empty()
            }
          }
          ?: Stream.empty<SettingObject>()
}

class ObjectSelectorDataProvider(
    private val type: String,
    private val dataProvider: SettingObjectDataProvider,
    private val settings: List<Setting>
) : AbstractBackEndDataProvider<SettingObject, Unit>() {
  override fun sizeInBackEnd(query: Query<SettingObject, Unit>?) =
      query
          ?.let { dataProvider.countBySettings(type, settings) }
          ?: 0

  override fun fetchFromBackEnd(query: Query<SettingObject, Unit>?) =
      query
          ?.let {
            dataProvider
                .getBySettings(type, settings, PageRequest.of(query.page, query.pageSize))
                .stream()
          }
          ?: Stream.empty<SettingObject>()
}