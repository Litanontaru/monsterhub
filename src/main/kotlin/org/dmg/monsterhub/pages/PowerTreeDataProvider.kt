package org.dmg.monsterhub.pages

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery
import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.meta.FeatureContainer

class PowerTreeDataProvider(
    private val containerData: FeatureContainerData,
    private val meta: FeatureContainer
) : AbstractBackEndHierarchicalDataProvider<FeatureContainerVo, Unit>() {
  override fun hasChildren(item: FeatureContainerVo?) = when(item) {
    null -> !containerData.features.isEmpty()
    else -> item.hasChildren
  }

  override fun fetchChildrenFromBackEnd(query: HierarchicalQuery<FeatureContainerVo, Unit>?) = when (query?.parent) {
    null -> top().stream()
    else -> query.parent.children.stream()
  }

  fun top() = meta.containFeatureTypes.map { FeatureContainerVo(containerData, it) }

  override fun getChildCount(query: HierarchicalQuery<FeatureContainerVo, Unit>?) = when (query?.parent) {
    null -> meta.containFeatureTypes.size
    else -> query.parent.count
  }
}