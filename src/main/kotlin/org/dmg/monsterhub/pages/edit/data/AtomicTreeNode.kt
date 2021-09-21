package org.dmg.monsterhub.pages.edit.data

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery
import org.dmg.monsterhub.data.ContainerData
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.Creature.Companion.CREATURE
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
  var compactable = true

  abstract val isStopper: Boolean

  abstract fun detectCycle(obj: Any): Boolean
  fun detectCycleAll(obj: Any): Boolean {
    var node: AtomicTreeNode? = this
    while (node != null) {
      if (node.detectCycle(obj)) {
        return true
      }
      node = node.parent
    }
    return false
  }

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
  abstract fun removeChild(child: AtomicTreeNode, update: (Any) -> Any)

  fun last(): AtomicTreeNode {
    var result = this
    while (true) {
      if (!result.compactable || result.isStopper) {
        return result
      }
      result = (result.children.singleOrNull() ?: return result)
    }
  }

  fun compacted(): List<AtomicTreeNode> {
    val result = mutableListOf<AtomicTreeNode>()
    var node = this
    while (true) {
      result += node
      if (!node.compactable || node.isStopper) {
        return result
      }
      node = (node.children.singleOrNull() ?: return result)
    }
  }

  fun compactedName(): String = compacted().mapNotNull { it.name() }.joinToString(": ")

  fun compactRate(): Decimal? = compacted().mapNotNull { it.rate() }.find { it.isNotBlank() }
}

class AttributeTreeNode(
    parent: AtomicTreeNode?,
    var data: FeatureContainerData,
    val attribute: FeatureContainerItem
) : AtomicTreeNode(parent) {
  override val isStopper = !attribute.onlyOne

  override fun detectCycle(obj: Any): Boolean = false

  override fun name(): String? = attribute.name

  override fun rate(): Decimal? = children.mapNotNull { it.rate() }.takeIf { it.isNotEmpty() }?.reduce { acc, rate -> acc + rate }

  override fun canAdd() = !attribute.onlyOne || children.isEmpty()

  override fun canEdit() = false

  override fun canRemove() = parent?.let { it.compactable && !it.isStopper && it.canRemove() } ?: false

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

  override fun editableObject(): Any {
    throw UnsupportedOperationException()
  }

  override fun remove(update: (Any) -> Any) {
    if (canRemove()) {
      parent!!.remove(update)
    } else {
      throw UnsupportedOperationException()
    }
  }

  override fun removeChild(child: AtomicTreeNode, update: (Any) -> Any) {
    when (child) {
      is ValueTreeNode -> {
        data.features.remove(child.value)
        data = update(data) as FeatureContainerData

        children.remove(child)
      }
      else -> throw UnsupportedOperationException()
    }
  }
}

class ValueTreeNode(
    parent: AtomicTreeNode?,
    val value: FeatureData
) : AtomicTreeNode(parent) {
  override val isStopper: Boolean
    get() = children.singleOrNull() == null

  override fun detectCycle(obj: Any) = value == obj

  override fun name(): String? = when (value.feature) {
    is ContainerData -> {
      if (detectCycleAll(value.feature)) {
        value.shortDisplay()
      } else {
        value.displayConfig().takeIf { it.isNotBlank() }
      }
    }
    else -> value.shortDisplay()
  }

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
    parent?.removeChild(this, update)

    value.deleteOnly = true
    update(value)
  }

  override fun removeChild(child: AtomicTreeNode, update: (Any) -> Any) {
    remove(update)
  }
}

class NestedValueTreeNode(
    parent: AtomicTreeNode?,
    val feature: Feature
) : AtomicTreeNode(parent) {
  override val isStopper = true

  override fun detectCycle(obj: Any) = feature == obj

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
    parent?.removeChild(this, update)

    if (feature.hidden) {
      feature.deleteOnly = true
      update(feature)
    }
  }

  override fun removeChild(child: AtomicTreeNode, update: (Any) -> Any) {
    throw UnsupportedOperationException()
  }
}

class BaseOnTreeNode(
    parent: AtomicTreeNode?,
    var creature: Creature
) : AtomicTreeNode(parent) {
  override val isStopper = true

  override fun detectCycle(obj: Any) = false

  override fun name(): String = "Основа"

  override fun rate(): Decimal? = null

  override fun canAdd(): Boolean = true

  override fun canEdit(): Boolean = false

  override fun canRemove(): Boolean = false

  override fun canCreate(): Boolean = true

  override fun addableType(): String = CREATURE

  override fun add(obj: Any, update: (Any) -> Any) = when (obj) {
    is FeatureData -> {
      when (val feature = obj.feature) {
        is Creature -> {
          creature.base.add(feature)
          creature = update(creature) as Creature

          feature.toTree(this).also {
            children.add(it)
          }
        }
        else -> throw UnsupportedOperationException("Unknown type of data ${obj.feature}")
      }
    }
    else -> throw UnsupportedOperationException("Unknown type of data $obj")
  }

  override fun editableObject() = false

  override fun remove(update: (Any) -> Any) {
    throw UnsupportedOperationException()
  }

  override fun removeChild(child: AtomicTreeNode, update: (Any) -> Any) {
    when (child) {
      is NestedValueTreeNode -> {
        when (val baseCreature = child.feature) {
          is Creature -> {
            creature.base.remove(baseCreature)
            creature = update(creature) as Creature

            children.remove(child)
          }
          else -> throw UnsupportedOperationException()
        }
      }
      else -> throw UnsupportedOperationException()
    }
  }
}

//----------------------------------------------------------------------------------------------------------------------

fun ContainerData.toTree(parent: AtomicTreeNode?): AtomicTreeNode =
    NestedValueTreeNode(parent, this).also { node ->
      node.children = mutableListOf()
      when (this) {
        is Creature -> node.children.add(this.toBaseTree(node))
      }
      node.children.addAll(meta().asSequence().map { it.toTree(node, this) })
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
        is ContainerData -> {
          if (!node.detectCycleAll(theFeature)) {
            node.children.add(theFeature.toTree(node))
          }
        }
      }
    }

fun Creature.toBaseTree(parent: AtomicTreeNode?) =
    BaseOnTreeNode(parent, this).also { node ->
      node.children = base
          .asSequence()
          .map { it.toTree(node) }
          .toMutableList()
    }

//----------------------------------------------------------------------------------------------------------------------

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