package org.dmg.monsterhub.pages

import com.vaadin.flow.data.provider.AbstractDataProvider
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.Query
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.meta.FeatureContainerItem

class FeatureContainerItemDataProvider(
    val container: FeatureContainer,
    val save: (FeatureContainer) -> Unit
) : AbstractDataProvider<FeatureContainerItem, Unit>(), DataProvider<FeatureContainerItem, Unit> {
  override fun isInMemory(): Boolean = true

  override fun fetch(query: Query<FeatureContainerItem, Unit>?) =
      container.containFeatureTypes.stream()
          .skip(query?.offset?.toLong() ?: 0)
          .limit(query?.limit?.toLong() ?: 0)

  override fun size(query: Query<FeatureContainerItem, Unit>?) = container.containFeatureTypes.size

  fun add(obj: FeatureContainerItem) {
    container.containFeatureTypes.add(obj)
    save(container)
    refreshAll()
  }

  fun update(obj: FeatureContainerItem) {
    save(container)
    refreshItem(obj)
  }

  fun delete(obj: FeatureContainerItem) {
    container.containFeatureTypes.remove(obj)
    save(container)
    refreshAll()
  }
}