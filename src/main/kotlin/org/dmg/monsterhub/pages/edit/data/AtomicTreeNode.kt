package org.dmg.monsterhub.pages.edit.data

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery
import org.dmg.monsterhub.data.ContainerData
import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.service.Decimal
import java.util.stream.Stream

abstract class AtomicTreeNode(
    var parent: AtomicTreeNode?
) {
  lateinit var children: MutableList<AtomicTreeNode>

  abstract val isStopper: Boolean

  abstract fun name(): String?
  abstract fun rate(): Decimal?

  abstract fun canAdd(): Boolean
  abstract fun canEdit(): Boolean
  abstract fun canRemove(): Boolean
  abstract fun canCreate(): Boolean

  abstract fun addableType(): String?
  abstract fun add(obj: Any, update: (Any) -> Any): AtomicTreeNode
  abstract fun editableObject(): Any
  abstract fun remove(update: (Any) -> Any)

  fun last(): AtomicTreeNode {
    var result = this
    while (true) {
      if (result.isStopper) {
        return result
      }
      result = (result.children.singleOrNull() ?: return result)
    }
  }

  fun compactedName(): String {
    val result = mutableListOf<String?>()
    var node = this
    while (true) {
      if (node.isStopper) {
        node.name()?.let { result += it }
        return result.joinToString(",")
      }
      val next = node.children.singleOrNull()
      if (next == null) {
        node.name()?.let { result += it }
        return result.joinToString(",")
      }
      node = next
    }
  }
}

class AttributeTreeNode(
    parent: AtomicTreeNode?,
    var data: FeatureContainerData,
    val attribute: FeatureContainerItem
) : AtomicTreeNode(parent) {
  override val isStopper = !attribute.onlyOne

  override fun name(): String? = attribute.name

  override fun rate(): Decimal? = children.mapNotNull { it.rate() }.takeIf { it.isNotEmpty() }?.reduce { acc, rate -> acc + rate }

  override fun canAdd() = !attribute.onlyOne || children.isEmpty()

  override fun canEdit() = false

  override fun canRemove() = false

  override fun canCreate() = canAdd() && attribute.allowHidden

  override fun addableType(): String? = attribute.featureType

  override fun add(obj: Any, update: (Any) -> Any) = when (obj) {
    is FeatureData -> {
      update(obj)
      data.features.add(obj)
      data = update(data) as FeatureContainerData

      obj.toTree(this).also {
        children.add(it)
      }
    }
    else -> throw UnsupportedOperationException("Unknown type of data $obj")
  }

  fun remove(obj: ValueTreeNode, update: (Any) -> Any) {
    data.features.remove(obj.value)
    data = update(data) as FeatureContainerData

    children.remove(obj)
  }

  override fun editableObject(): Any {
    throw UnsupportedOperationException()
  }

  override fun remove(update: (Any) -> Any) {
    throw UnsupportedOperationException()
  }
}

class ValueTreeNode(
    parent: AtomicTreeNode?,
    val value: FeatureData
) : AtomicTreeNode(parent) {
  override val isStopper: Boolean
    get() = children.singleOrNull()?.let { it !is NestedValueTreeNode } ?: true

  override fun name(): String? = value.shortDisplay().takeIf { isStopper }

  override fun rate(): Decimal? = value.rate()

  override fun canAdd() = false

  override fun canEdit() = true

  override fun canRemove() = true

  override fun canCreate() = false

  override fun addableType(): String? = null

  override fun add(obj: Any, update: (Any) -> Any): AtomicTreeNode {
    throw UnsupportedOperationException()
  }

  override fun editableObject(): Any = value

  override fun remove(update: (Any) -> Any) {
    (parent as AttributeTreeNode).remove(this, update)

    value.deleteOnly = true
    update(value)
  }
}

class NestedValueTreeNode(
    parent: AtomicTreeNode?,
    val feature: Feature
) : AtomicTreeNode(parent) {
  override val isStopper = true

  override fun name(): String? = feature.name

  override fun rate(): Decimal? = feature.rate()

  override fun canAdd() = false

  override fun canEdit() = true

  override fun canRemove() = true

  override fun canCreate() = false

  override fun addableType(): String? = null

  override fun add(obj: Any, update: (Any) -> Any): AtomicTreeNode {
    throw UnsupportedOperationException()
  }

  override fun editableObject() = feature

  override fun remove(update: (Any) -> Any) {
    parent?.remove(update)

    if (feature.hidden) {
      feature.deleteOnly = true
      update(feature)
    }
  }
}

fun ContainerData.toTree(parent: AtomicTreeNode?): AtomicTreeNode =
    NestedValueTreeNode(parent, this).also { node ->
      node.children = meta()
          .asSequence()
          .map { it.toTree(node, this) }
          .toMutableList()
    }

fun FeatureContainerItem.toTree(parent: AtomicTreeNode?, container: FeatureContainerData): AtomicTreeNode =
    AttributeTreeNode(parent, container, this).also { node ->
      node.children = container
          .features
          .asSequence()
          .filter { it.feature.featureType == this.featureType }
          .map { it.toTree(node) }
          .toMutableList()
    }

fun FeatureData.toTree(parent: AtomicTreeNode?): AtomicTreeNode =
    ValueTreeNode(parent, this).also { node ->
      node.children = meta()
          .asSequence()
          .map { it.toTree(node, this) }
          .toMutableList()

      when (val theFeature = feature) {
        is ContainerData -> node.children.add(theFeature.toTree(node))
      }
    }

class AtomicTreeNodeDataProvider(
    val root: AtomicTreeNode
) : AbstractBackEndHierarchicalDataProvider<AtomicTreeNode, Unit>() {
  override fun hasChildren(item: AtomicTreeNode?) = item?.last()?.children?.isNotEmpty() ?: false

  override fun fetchChildrenFromBackEnd(query: HierarchicalQuery<AtomicTreeNode, Unit>?): Stream<AtomicTreeNode> = when (query?.parent) {
    null -> root.children.stream()
    else -> query.parent.last().children.stream()
  }

  override fun getChildCount(query: HierarchicalQuery<AtomicTreeNode, Unit>?) = when (query?.parent) {
    null -> root.children.size
    else -> query.parent.last().children.size
  }
}