package org.dmg.monsterhub.pages.edit.data

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
    else -> item.children.isNotEmpty()
  }

  override fun fetchChildrenFromBackEnd(query: HierarchicalQuery<FeatureContainerVo, Unit>?) = when (query?.parent) {
    null -> top().stream()
    else -> query.parent.children.stream()
  }

  fun top() = meta.containFeatureTypes.map { FeatureContainerVo(containerData, it, null) }

  override fun getChildCount(query: HierarchicalQuery<FeatureContainerVo, Unit>?) = when (query?.parent) {
    null -> top().size
    else -> query.parent.count
  }

  fun add(new: FeatureContainerVo, parent: FeatureContainerVo) {

    refreshItem(parent, true)
  }
}