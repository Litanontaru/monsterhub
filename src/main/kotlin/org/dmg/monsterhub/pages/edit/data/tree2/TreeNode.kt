package org.dmg.monsterhub.pages.edit.data.tree2

import org.dmg.monsterhub.api.TreeObject
import org.dmg.monsterhub.api.TreeObjectAttribute
import org.dmg.monsterhub.api.TreeObjectOption
import org.dmg.monsterhub.api.TreeObjectType
import org.dmg.monsterhub.service.Decimal

abstract class TreeNode(
    val parent: TreeNode?
) {
  abstract fun name(): String?
  abstract fun rate(): Decimal?

  abstract fun hasChildren(): Boolean
  abstract fun children(showEmpty: Boolean): List<TreeNode>
  abstract fun count(showEmpty: Boolean): Int
  abstract fun canCompact(): Boolean

  open fun add(option: TreeObjectOption) {
    throw UnsupportedOperationException()
  }

  open fun remove(node: TreeNode) {
    throw UnsupportedOperationException()
  }

  open fun replace(option: TreeObjectOption) {
    throw UnsupportedOperationException()
  }

  open fun dictionary(): List<String> {
    throw UnsupportedOperationException()
  }

  open fun canCreate(): Boolean = false

  open fun value(): MutableList<Any?> {
    throw UnsupportedOperationException()
  }

  open fun setValue(index: Int, new: Any?) {
    throw UnsupportedOperationException()
  }

  fun last(): TreeNode {
    var node = this
    while (node.canCompact()) {
      node = node.children(true)[0]
    }
    return node
  }

  fun compacted(): Sequence<TreeNode> {
    var node = this
    var result = sequenceOf(node)
    while (node.canCompact()) {
      node = node.children(true)[0]
      result += node
    }
    return result
  }
}

class TreeObjectNode(
    parent: TreeNode?,
    val obj: TreeObject
) : TreeNode(parent) {

  override fun name() = obj.name

  override fun rate() = obj.rate

  override fun hasChildren() = obj.attributes.isNotEmpty()

  override fun children(showEmpty: Boolean) = obj.attributes.filter { showEmpty || !it.isEmpty() }.map { it.toNode(this) }

  override fun count(showEmpty: Boolean) = obj.attributes.filter { showEmpty || !it.isEmpty() }.size

  override fun canCompact() = count(true) == 1

  override fun value(): MutableList<Any?> = obj.primitive

  override fun setValue(index: Int, new: Any?) {
    obj.setPrimitive(index, new)
  }
}

class TerminalTreeObjectAttributeNode(
    parent: TreeNode?,
    private val attribute: TreeObjectAttribute
) : TreeNode(parent) {
  val type = attribute.type

  override fun name() = attribute.name

  override fun rate(): Decimal? = null

  override fun hasChildren() = false

  override fun children(showEmpty: Boolean) = listOf<TreeNode>()

  override fun count(showEmpty: Boolean) = 0

  override fun canCompact() = false

  override fun value(): MutableList<Any?> = attribute.primitive

  override fun setValue(index: Int, new: Any?) {
    attribute.setPrimitive(index, new)
  }
}

class NonTerminalTreeObjectAttributeNode(
    parent: TreeNode?,
    private val attribute: TreeObjectAttribute
) : TreeNode(parent) {
  private var data: List<TreeObject> = attribute.get()
  private var isEmpty = attribute.isEmpty()

  val type = attribute.type

  override fun name() = attribute.name

  override fun rate(): Decimal? = attribute.rate

  override fun hasChildren() = data.isNotEmpty()

  override fun children(showEmpty: Boolean) = data.map { TreeObjectNode(this, it) }

  override fun count(showEmpty: Boolean) = data.size

  override fun canCompact() = attribute.type != TreeObjectType.MULTIPLE_REF && count(true) == 1

  override fun add(option: TreeObjectOption) {
    data = data + attribute.add(option)
    isEmpty = data.isEmpty()
  }

  override fun remove(node: TreeNode) {
    val obj = (node as TreeObjectNode).obj
    attribute.remove(obj)
    data = data - obj
    isEmpty = data.isEmpty()
  }

  override fun replace(option: TreeObjectOption) {
    val replace = attribute.replace(option)
    data = listOf(replace)
    isEmpty = data.isEmpty()
  }

  override fun dictionary(): List<String> = attribute.dictionary

  override fun canCreate(): Boolean = attribute.canCreate
}

fun TreeObjectAttribute.toNode(parent: TreeNode?): TreeNode = when {
  type.terminal -> TerminalTreeObjectAttributeNode(parent, this)
  else -> NonTerminalTreeObjectAttributeNode(parent, this)
}