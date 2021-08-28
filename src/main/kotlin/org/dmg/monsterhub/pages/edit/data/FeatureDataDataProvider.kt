package org.dmg.monsterhub.pages.edit.data

import com.vaadin.flow.data.provider.AbstractDataProvider
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.Query
import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.FeatureData

class FeatureDataDataProvider(
    val featureType: String,
    val containerData: FeatureContainerData,
    val save: (FeatureContainerData) -> Unit
) : AbstractDataProvider<FeatureData, Unit>(), DataProvider<FeatureData, Unit> {
  override fun isInMemory(): Boolean = true

  override fun fetch(query: Query<FeatureData, Unit>?) =
      containerData.features.stream()
          .filter { it.feature.featureType == featureType }
          .skip(query?.offset?.toLong() ?: 0)
          .limit(query?.limit?.toLong() ?: 0)

  override fun size(query: Query<FeatureData, Unit>?) =
      containerData.features
          .filter { it.feature.featureType == featureType }
          .size

  fun add(obj: FeatureData) {
    containerData.features.add(obj)
    save(containerData)
    refreshAll()
  }

  fun update(obj: FeatureData) {
    save(containerData)
    refreshItem(obj)
  }

  fun delete(obj: FeatureData) {
    containerData.features.remove(obj)
    save(containerData)
    refreshAll()
  }
}