package org.dmg.monsterhub.pages.edit.data

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider
import com.vaadin.flow.data.provider.Query
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.repository.SettingRepository
import java.util.stream.Stream

class SettingBackEndDataProvider(
    val repository: SettingRepository
) : AbstractBackEndDataProvider<Setting, String>() {
  override fun sizeInBackEnd(query: Query<Setting, String>?) =
      query
          ?.filter
          ?.orElse(null)
          ?.let {
            query.page
            query.pageSize
            when {
              it.isNotBlank() -> repository.countByNameContaining(it)
              else -> 0
            }
          }
          ?: 0

  override fun fetchFromBackEnd(query: Query<Setting, String>?) =
      query
          ?.filter
          ?.orElse(null)
          ?.let {
            query.page
            query.pageSize
            when {
              it.isNotBlank() -> repository.findAllByNameContaining(it).stream()
              else -> Stream.empty()
            }
          }
          ?: Stream.empty<Setting>()
}