package org.dmg.monsterhub.pages.edit.data.tree2

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery
import java.util.stream.Stream

class TreeDataProvider(
    val root: TreeNode
) : AbstractBackEndHierarchicalDataProvider<TreeNode, Unit>() {
  override fun hasChildren(item: TreeNode?): Boolean = (item ?: root).last().hasChildren()

  override fun fetchChildrenFromBackEnd(query: HierarchicalQuery<TreeNode, Unit>?): Stream<TreeNode> = (query?.parent
      ?: root).last().children().stream()

  override fun getChildCount(query: HierarchicalQuery<TreeNode, Unit>?): Int = (query?.parent ?: root).last().count()
}