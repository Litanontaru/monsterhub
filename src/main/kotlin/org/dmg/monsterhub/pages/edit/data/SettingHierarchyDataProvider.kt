package org.dmg.monsterhub.pages.edit.data

import com.vaadin.flow.data.provider.AbstractDataProvider
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.Query
import org.dmg.monsterhub.data.setting.Setting
import java.util.stream.Stream

class SettingHierarchyDataProvider(
    val hierarchical: Setting,
    val save: (Setting) -> Unit
) : AbstractDataProvider<Setting, Unit>(), DataProvider<Setting, Unit> {
  override fun isInMemory(): Boolean = true

  override fun fetch(query: Query<Setting, Unit>?): Stream<Setting> =
      hierarchical.base.distinct().stream()
          .skip(query?.offset?.toLong() ?: 0)
          .limit(query?.limit?.toLong() ?: 0)

  override fun size(query: Query<Setting, Unit>?): Int = hierarchical.base.distinct().size

  fun add(obj: Setting) {
    hierarchical.base.add(obj)
    hierarchical.base = hierarchical.base.distinct().toMutableList()
    save(hierarchical)
    refreshAll()
  }

  fun delete(obj: Setting) {
    hierarchical.base = hierarchical.base.distinct().toMutableList()
    hierarchical.base.remove(obj)
    save(hierarchical)
    refreshAll()
  }
}