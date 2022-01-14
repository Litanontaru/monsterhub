package org.dmg.monsterhub.pages.edit.form.space2

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.api.TreeObjectType
import org.dmg.monsterhub.data.meta.NumberOption
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.data.tree2.NonTerminalTreeObjectAttributeNode
import org.dmg.monsterhub.pages.edit.data.tree2.TerminalTreeObjectAttributeNode
import org.dmg.monsterhub.pages.edit.data.tree2.TreeNode
import org.dmg.monsterhub.pages.edit.data.tree2.TreeObjectNode
import org.dmg.monsterhub.pages.edit.form.space.TreeObjectOptionSelection
import java.math.BigDecimal

object Lines {
  fun toComponent(
    item: TreeNode,
    editing: Boolean,
    locator: ServiceLocator,
    refreshItem: (TreeNode, Boolean) -> Unit
  ): HorizontalLayout = item
    .compacted()
    .toList()
    .map {
      toComponent(it).also {
        it.locator = locator
        it.refreshItem = refreshItem
      }
    }
    .flatMap { it.getElements(editing) }
    .fold<LineElement, List<LineElement>>(listOf()) { result, element ->
      if (result.isEmpty()) {
        listOf(element)
      } else {
        result.subList(0, result.size - 1) + result.last().concat(element)
      }
    }
    .flatMap { it.toComponents() }
    .let {
      HorizontalLayout().apply {
        add(*it.toTypedArray())
        setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, *it.toTypedArray())

        width = "100%"
      }
    }

  private fun toComponent(node: TreeNode): EditableLine = when (node) {
    is TerminalTreeObjectAttributeNode -> toComponent(node, node.type)
    is NonTerminalTreeObjectAttributeNode -> toComponent(node, node.type)
    is TreeObjectNode -> toComponent(node, node.obj.type)
    else -> EditableLine()
  }

  private fun toComponent(node: TreeNode, type: TreeObjectType): EditableLine = when (type) {
    TreeObjectType.POSITIVE -> PositiveLine(node)
    TreeObjectType.POSITIVE_AND_INFINITE -> PositiveAndInfiniteLine(node)
    TreeObjectType.FREE -> FreeLine(node)
    TreeObjectType.DAMAGE -> DamageLine(node)
    TreeObjectType.ARMOR -> ArmorLine(node)
    TreeObjectType.IMPORTANCE -> ImportanceLine(node)
    TreeObjectType.LINE -> LineLine(node)
    TreeObjectType.MULTILINE -> MultilineLine(node)
    TreeObjectType.FEATURE -> EditableLine()
    TreeObjectType.SINGLE_REF -> RefLine(node)
    TreeObjectType.MULTIPLE_REF -> MultiRefLine(node)
    TreeObjectType.FEATURE_OBJECT -> FeatureLine(node)
    TreeObjectType.BASE_CREATURE -> BaseCreatureLine(node)
    TreeObjectType.FEATURE_DATA -> FeatureDataLine(node)
  }
}

interface LineElement {
  fun concat(right: LineElement): List<LineElement>

  fun toComponents(): List<Component>
}

class StringLineElement(private val value: String) : LineElement {
  override fun concat(right: LineElement): List<LineElement> = when (right) {
    is StringLineElement -> listOf(StringLineElement("$value ${right.value}"))
    else -> listOf(this, right)
  }

  override fun toComponents(): List<Component> = when {
    value.isBlank() -> listOf()
    else -> listOf(Label(value))
  }
}

class ComponentLineElement(vararg components: Component) : LineElement {
  private val list = components.toList()

  override fun concat(right: LineElement): List<LineElement> = when (right) {
    is ComponentLineElement -> listOf(ComponentLineElement(*(list + right.list).toTypedArray()))
    else -> listOf(this, right)
  }

  override fun toComponents(): List<Component> = list
}

open class EditableLine {
  lateinit var locator: ServiceLocator
  lateinit var refreshItem: (TreeNode, Boolean) -> Unit

  open fun getElements(editing: Boolean): List<LineElement> = listOf()
}

class PositiveLine(private val item: TreeNode) : EditableLine() {
  override fun getElements(editing: Boolean): List<LineElement> {
    val name = item.name()?.let { "$it: " } ?: ": "
    val initial = (item.value()[0]?.let { it as BigDecimal } ?: BigDecimal.ZERO).stripTrailingZeros().toPlainString()

    return if (editing) {
      val editField = TextField().apply {
        value = initial
        width = "6em"

        addValueChangeListener {
          val new = it.value.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO
          item.setValue(0, new)
          item.value()[0] = new
        }
      }

      listOf(StringLineElement(name), ComponentLineElement(editField))
    } else {
      listOf(StringLineElement(name + initial))
    }
  }
}

class PositiveAndInfiniteLine(private val item: TreeNode) : EditableLine() {
  override fun getElements(editing: Boolean): List<LineElement> {
    val name = item.name()?.let { "$it: " } ?: ": "
    val initial = (item.value()[0]?.let { it as BigDecimal } ?: BigDecimal.ZERO)

    return if (editing) {
      val (field, infinite) = valueWithInfinite(initial) {
        item.setValue(0, it)
        item.value()[0] = it
      }

      listOf(StringLineElement(name), ComponentLineElement(field, infinite))
    } else {
      listOf(StringLineElement(name + positiveWithInfiniteLabel(initial)))
    }
  }
}

class FreeLine(private val item: TreeNode) : EditableLine() {
  override fun getElements(editing: Boolean): List<LineElement> {
    val name = item.name()?.let { "$it: " } ?: ": "
    val initial = (item.value()[0]?.let { it as BigDecimal } ?: BigDecimal.ZERO).stripTrailingZeros().toPlainString()
    return if (editing) {
      val editField = TextField().apply {
        value = initial
        width = "6em"

        addValueChangeListener {
          val new = it.value.toBigDecimalOrNull() ?: BigDecimal.ZERO
          item.setValue(0, new)
          item.value()[0] = new
        }
      }

      listOf(StringLineElement(name), ComponentLineElement(editField))
    } else {
      listOf(StringLineElement(name + initial))
    }
  }
}

class DamageLine(private val item: TreeNode) : EditableLine() {
  override fun getElements(editing: Boolean): List<LineElement> {
    val name = item.name()?.let { "$it: " } ?: ": "
    val damageValue = (item.value()[0]?.let { it as BigDecimal }
      ?: BigDecimal.ZERO).stripTrailingZeros().toPlainString()
    val destructionValue = (item.value()[1]?.let { it as BigDecimal }
      ?: BigDecimal.ZERO).stripTrailingZeros().toPlainString()

    return if (editing) {
      val damage = TextField().apply {
        value = damageValue
        width = "6em"
        addValueChangeListener {
          val new = it.value.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO
          item.setValue(0, new)
          item.value()[0] = new
        }
      }
      val destruction = TextField().apply {
        value = destructionValue
        width = "6em"
        addValueChangeListener {
          val new = it.value.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO
          item.setValue(1, new)
          item.value()[1] = new
        }
      }
      listOf(
        StringLineElement(name),
        ComponentLineElement(damage),
        StringLineElement("/"),
        ComponentLineElement(destruction)
      )
    } else {
      listOf(StringLineElement("$name$damageValue / $destructionValue"))
    }
  }
}

class ArmorLine(private val item: TreeNode) : EditableLine() {
  override fun getElements(editing: Boolean): List<LineElement> {
    val name = item.name()?.let { "$it: " } ?: ": "
    val strongValue = item.value()[0]?.let { it as BigDecimal } ?: BigDecimal.ZERO
    val standardValue = item.value()[1]?.let { it as BigDecimal } ?: BigDecimal.ZERO
    val weakValue = item.value()[2]?.let { it as BigDecimal } ?: BigDecimal.ZERO

    return if (editing) {
      val (field, infinite) = valueWithInfinite(strongValue) {
        item.setValue(0, it)
        item.value()[0] = it
      }
      val (fieldA, infiniteA) = valueWithInfinite(item.value()[1]?.let { it as BigDecimal }
        ?: BigDecimal.ZERO) {
        item.setValue(1, it)
        item.value()[1] = it
      }
      val (fieldB, infiniteB) = valueWithInfinite(item.value()[2]?.let { it as BigDecimal }
        ?: BigDecimal.ZERO) {
        item.setValue(2, it)
        item.value()[2] = it
      }

      listOf(
        StringLineElement(name),
        ComponentLineElement(field, infinite),
        StringLineElement("/"),
        ComponentLineElement(fieldA, infiniteA),
        StringLineElement("/"),
        ComponentLineElement(fieldB, infiniteB)
      )
    } else {
      listOf(
        StringLineElement(
          name + positiveWithInfiniteLabel(strongValue) + "/" + positiveWithInfiniteLabel(
            standardValue
          ) + "/" + positiveWithInfiniteLabel(weakValue)
        )
      )
    }
  }
}

class ImportanceLine(private val item: TreeNode) : EditableLine() {
  override fun getElements(editing: Boolean): List<LineElement> {
    val name = item.name()?.let { "$it: " } ?: ": "
    val initial = (item.value()[0]?.let { it as BigDecimal } ?: BigDecimal.ZERO).toInt()

    return if (editing) {
      val edit = ComboBox<Int>().apply {
        width = "15em"

        setItems((0..9).toList())
        setItemLabelGenerator { NumberOption.IMPORTANCE_OPTIONS[it] }
        this.value = initial
        addValueChangeListener {
          val new = it.value.toBigDecimal()
          item.setValue(0, new)
          item.value()[0] = new
        }
      }

      listOf(StringLineElement(name), ComponentLineElement(edit))
    } else {
      listOf(StringLineElement(name + NumberOption.IMPORTANCE_OPTIONS[initial]))
    }
  }
}

class LineLine(private val item: TreeNode) : EditableLine() {
  override fun getElements(editing: Boolean): List<LineElement> {
    val name = item.name()?.let { "$it:" } ?: ""
    val initial = (item.value()[0]?.let { it as String } ?: "")
    return if (editing) {
      val editField = TextField().apply {
        value = initial
        width = "25em"

        addValueChangeListener {
          val new = it.value
          item.setValue(0, new)
          item.value()[0] = new
        }
      }
      listOf(StringLineElement(name), ComponentLineElement(editField))
    } else {
      listOf(StringLineElement("$name $initial"))
    }
  }
}

class MultilineLine(private val item: TreeNode) : EditableLine() {
  override fun getElements(editing: Boolean): List<LineElement> {
    val name = item.name()?.let { "$it:" } ?: ""
    val initial = (item.value()[0]?.let { it as String } ?: "")
    return if (editing) {
      val editField = TextArea().apply {
        value = initial
        width = "25em"

        addValueChangeListener {
          val new = it.value
          item.setValue(0, new)
          item.value()[0] = new
        }
      }
      listOf(StringLineElement(name), ComponentLineElement(editField))
    } else {
      listOf(StringLineElement("$name $initial"))
    }
  }
}

open class RefLine(private val item: TreeNode) : EditableLine() {
  override fun getElements(editing: Boolean): List<LineElement> {
    val name = item.name()?.let { "$it:" } ?: ""

    return if (editing && canAdd()) {
      val addButton = Button(Icon(VaadinIcon.PLUS)) {
        TreeObjectOptionSelection(
          locator.treeObjectDictionary,
          item.dictionary(),
          locator.settings.map { it.id },
          null
        ) {
          item.add(it)
          refreshItem(item, true)
        }.open()
      }
      if (item.canCreate()) {
        val createButton = Button(Icon(VaadinIcon.MAGIC)) {
          val new = locator.treeObjectDictionary.create(item.dictionary().single(), locator.setting.id)
          item.add(new)
          refreshItem(item, true)
        }

        listOf(StringLineElement(name), ComponentLineElement(addButton, createButton))
      } else {
        listOf(StringLineElement(name), ComponentLineElement(addButton))
      }
    } else {
      listOf(StringLineElement(name))
    }
  }

  open fun canAdd() = !item.hasChildren()
}

class MultiRefLine(item: TreeNode) : RefLine(item) {
  override fun canAdd(): Boolean = true
}

class FeatureDataLine(private val item: TreeNode) : EditableLine() {
  override fun getElements(editing: Boolean): List<LineElement> {
    val name = item.name() ?: ""
    return if (editing) {

      val removeButton = Button(Icon(VaadinIcon.CLOSE)) {
        item.parent!!.remove(item)
        refreshItem(item.parent, true)
      }

      listOf(StringLineElement(name), ComponentLineElement(removeButton))
    } else {
      listOf(StringLineElement(name))
    }
  }
}

class FeatureLine(private val item: TreeNode) : EditableLine() {
  override fun getElements(editing: Boolean): List<LineElement> {
    val name = (item.value()[0]?.let { it as String } ?: "")
    return if (editing) {
      val editField = TextField().apply {
        value = name
        width = "25em"

        addValueChangeListener {
          val new = it.value
          item.setValue(0, new)
          item.value()[0] = new
        }
      }
      listOf(ComponentLineElement(editField))
    } else {
      listOf(StringLineElement(name))
    }
  }
}

class BaseCreatureLine(private val item: TreeNode) : EditableLine() {
  override fun getElements(editing: Boolean): List<LineElement> {
    val name = (item.value()[0]?.let { it as String } ?: "")
    return if (editing) {
      val removeButton = Button(Icon(VaadinIcon.CLOSE)) {
        item.parent!!.remove(item)
        refreshItem(item.parent, true)
      }

      listOf(StringLineElement(name), ComponentLineElement(removeButton))
    } else {
      listOf(StringLineElement(name))
    }
  }
}

//----------------------------------------------------------------------------------------------------------------------

private fun positiveWithInfiniteLabel(value: BigDecimal): String =
  if (value == Int.MAX_VALUE.toBigDecimal()) "Бесконечность" else value.stripTrailingZeros().toPlainString()

private fun valueWithInfinite(value: BigDecimal, setter: (BigDecimal) -> Unit): Pair<TextField, Checkbox> {
  val isInfinite = value == Int.MAX_VALUE.toBigDecimal()

  val field = TextField().apply {
    width = "6em"

    this.value = if (isInfinite) "" else value.stripTrailingZeros().toPlainString()
    isEnabled = !isInfinite

    addValueChangeListener {
      setter(it.value.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO)
    }

    width = "10em"
  }
  val inifinite = Checkbox("Бесконечность").apply {
    this.value = isInfinite

    addValueChangeListener {
      if (it.value) {
        field.value = ""
        field.isEnabled = false

        setter(Int.MAX_VALUE.toBigDecimal())
      } else {
        field.value = "0"
        field.isEnabled = true

        setter(BigDecimal.ZERO)
      }
    }
  }

  return field to inifinite
}