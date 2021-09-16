package org.dmg.monsterhub.pages.edit.data

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery
import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.service.Decimal
import java.util.stream.Stream

abstract class PowerTreeNode(
    var parent: PowerTreeNode?
) {
  lateinit var children: MutableList<PowerTreeNode>

  abstract val isStopper: Boolean

  abstract fun name(): String?
  abstract fun rate(): Decimal?

  abstract fun canAdd(): Boolean
  abstract fun canEdit(): Boolean
  abstract fun canRemove(): Boolean

  abstract fun addableType(): String?
  abstract fun add(obj: Any, update: (Any) -> Any): PowerTreeNode
  abstract fun editableObject(): Any
  abstract fun remove(update: (Any) -> Any)

  fun compact(parent: CompactNode?): CompactNode {
    var take = this
    val values = mutableListOf<PowerTreeNode>()
    do {
      values += take
      if (take.isStopper) break
      take = take.children.singleOrNull() ?: break
    } while (true)

    return CompactNode(values, parent).also { node ->
      node.children = values.last().children.asSequence().map { it.compact(node) }.toMutableList()
    }
  }
}

class AttributeTreeNode(
    parent: PowerTreeNode?,
    var data: FeatureContainerData,
    val attribute: FeatureContainerItem
) : PowerTreeNode(parent) {
  override val isStopper = !attribute.onlyOne

  override fun name(): String? = attribute.name

  override fun rate(): Decimal? = children.mapNotNull { it.rate() }.takeIf { it.isNotEmpty() }?.reduce { acc, rate -> acc + rate }

  override fun canAdd() = !attribute.onlyOne || children.isEmpty()

  override fun canEdit() = false

  override fun canRemove() = false

  override fun addableType(): String? = attribute.featureType

  override fun add(obj: Any, update: (Any) -> Any) = when (obj) {
    is FeatureData -> {
      data.features.add(obj)
      data = update(data) as FeatureContainerData
      obj.toTree(this).also {
        children.add(it)
      }
    }
    else -> throw UnsupportedOperationException("Unknown type of data $obj")
  }

  fun remove(obj: ValueTreeNode, update: (Any) -> Any): PowerTreeNode {
    data.features.remove(obj.value)
    data = update(data) as FeatureContainerData
    children.remove(obj)
    return obj
  }

  override fun editableObject(): Any {
    throw UnsupportedOperationException()
  }

  override fun remove(update: (Any) -> Any) {
    throw UnsupportedOperationException()
  }
}

class ValueTreeNode(
    parent: PowerTreeNode?,
    val value: FeatureData
) : PowerTreeNode(parent) {
  override val isStopper: Boolean
    get() = children.singleOrNull()?.let { it !is NestedValueTreeNode } ?: true

  override fun name(): String? = value.display().takeIf { isStopper }

  override fun rate(): Decimal? = value.rate()

  override fun canAdd() = false

  override fun canEdit() = true

  override fun canRemove() = true

  override fun addableType(): String? = null

  override fun add(obj: Any, update: (Any) -> Any): PowerTreeNode {
    throw UnsupportedOperationException()
  }

  override fun editableObject(): Any = value

  override fun remove(update: (Any) -> Any) {
    (parent as AttributeTreeNode).remove(this, update)
  }
}

class NestedValueTreeNode(
    parent: PowerTreeNode?,
    val feature: Feature
) : PowerTreeNode(parent) {
  override val isStopper = true

  override fun name(): String? = feature.name

  override fun rate(): Decimal? = feature.rate()

  override fun canAdd() = false

  override fun canEdit() = true

  override fun canRemove() = false

  override fun addableType(): String? = null

  override fun add(obj: Any, update: (Any) -> Any): PowerTreeNode {
    throw UnsupportedOperationException()
  }

  override fun editableObject() = feature

  override fun remove(update: (Any) -> Any) {
    throw UnsupportedOperationException()
  }
}

fun Power.toTree(parent: PowerTreeNode?): PowerTreeNode =
    NestedValueTreeNode(parent, this).also { node ->
      node.children = meta()
          .asSequence()
          .map { it.toTree(node, this) }
          .toMutableList()
    }

fun FeatureContainerItem.toTree(parent: PowerTreeNode?, container: FeatureContainerData): PowerTreeNode =
    AttributeTreeNode(parent, container, this).also { node ->
      node.children = container
          .features
          .asSequence()
          .filter { it.feature.featureType == this.featureType }
          .map { it.toTree(node) }
          .toMutableList()
    }

fun FeatureData.toTree(parent: PowerTreeNode?): PowerTreeNode =
    ValueTreeNode(parent, this).also { node ->
      node.children = meta()
          .asSequence()
          .map { it.toTree(node, this) }
          .toMutableList()

      when (val theFeature = feature) {
        is Power -> node.children.add(theFeature.toTree(node))
      }
    }

class CompactNode(
    val data: MutableList<PowerTreeNode>,
    val parent: CompactNode?
) {
  lateinit var children: MutableList<CompactNode>

  fun name(): String? = data.mapNotNull { it.name() }.joinToString(" : ")

  fun rate(): Decimal? = data.last().rate()

  fun canAdd(): Boolean = data.any { it.canAdd() }
  fun canEdit(): Boolean = data.any { it.canEdit() }
  fun canRemove(): Boolean = data.any { it.canRemove() }

  fun addableType(): String? = data.reversed().mapNotNull { it.addableType() }.first()
  fun editableObject(): Any = data.reversed().find { it.canEdit() }?.editableObject()
      ?: throw UnsupportedOperationException()

  fun add(obj: Any, update: (Any) -> Any) {
    data
        .reversed()
        .find { it.canAdd() }
        ?.also {
          val node = it.add(obj, update)
          if (it.isStopper) {
            children.add(node.compact(this))
          } else {
            data.add(node)
          }
        }
  }

  fun remove(update: (Any) -> Any) {
    data
        .reversed()
        .find { it.canRemove() }
        ?.also { node ->
          if (node.parent!!.isStopper) {
            parent!!.children.removeIf { it.data.contains(node) }
          } else {
            parent!!.data.remove(node)
          }
          node.remove(update)
        }
  }

  override fun toString(): String = "${name()} = ${rate()}"
}

class CompactNodeDataProvider(
    val root: CompactNode
) : AbstractBackEndHierarchicalDataProvider<CompactNode, Unit>() {
  override fun hasChildren(item: CompactNode?) = item?.children?.isNotEmpty() ?: false

  override fun fetchChildrenFromBackEnd(query: HierarchicalQuery<CompactNode, Unit>?): Stream<CompactNode> = when (query?.parent) {
    null -> root.children.stream()
    else -> query.parent.children.stream()
  }

  override fun getChildCount(query: HierarchicalQuery<CompactNode, Unit>?) = when (query?.parent) {
    null -> root.children.size
    else -> query.parent.children.size
  }
}