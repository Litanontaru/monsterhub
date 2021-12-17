package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.treegrid.TreeGrid
import org.dmg.monsterhub.api.TreeObjectDictionary
import org.dmg.monsterhub.api.TreeObjectOption
import org.dmg.monsterhub.api.TreeObjectType
import org.dmg.monsterhub.data.ContainerData
import org.dmg.monsterhub.data.meta.NumberOption
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.data.tree2.*
import java.math.BigDecimal

object TreeSpace2 : Space {
  override fun support(obj: Any): Boolean = obj is ContainerData

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val parent = mutableListOf<Component>()
    val obj = anyObj as ContainerData

    val root = locator.treeObjectController.get(obj.id)
    val dataProvider = TreeDataProvider(TreeObjectNode(null, root))

    dataProvider.showEmpty = locator.config.spaces[TreeSpace2]?.let { it as Boolean } ?: false

    parent.add(TreeGrid<TreeNode>().apply {
      //ADD SELECTION TRACKER
      var selectedItem: TreeNode? = null
      fun updateSelection(node: TreeNode?) {
        if (node == null || selectedItem == null || node != selectedItem) {
          val oldSelected = selectedItem
          selectedItem = node
          if (oldSelected != null) {
            dataProvider.refreshItem(oldSelected)
          }
          if (selectedItem != null) {
            dataProvider.refreshItem(selectedItem)
          }
        }
      }

      addSelectionListener { updateSelection(it.firstSelectedItem.orElse(null)) }

      addComponentHierarchyColumn { item ->
        HorizontalLayout().apply {
          val compacted = item.compacted().toList()
          val elements = (listOf(-1) + compacted
              .withIndex()
              .filter {
                when (val node = it.value) {
                  is TreeObjectNode -> node.obj.type == TreeObjectType.FEATURE_DATA
                  else -> false
                }
              }
              .map { it.index } + (compacted.size - 1))
              .distinct()
              .windowed(2, 1)
              .flatMap {
                val text = IntRange(it.first() + 1, it.last()).mapNotNull { compacted[it].name() }.filter { it.isNotBlank() }.joinToString(" ")
                val actions = Actions(compacted[it.last()])
                    .also {
                      it.locator = locator
                      it.editing = item == selectedItem
                      it.refreshItem = { item, refreshChildren -> dataProvider.refreshItem(item, refreshChildren) }
                    }
                listOf<Component>(Label(text), actions)
              }
              .toTypedArray()

          add(*elements)
          setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, *elements)
        }
      }.also {
        it.isAutoWidth = true
      }
      addColumn {
        it.compacted().mapNotNull { it.rate() }.find { it.isNotBlank() }
      }.also {
        it.width = "6em"
        it.flexGrow = 0
      }

      ContextMenu().also {
        it.addItem("Срыть/показать пустое") {
          dataProvider.showEmpty = !dataProvider.showEmpty
          locator.config.spaces[TreeSpace2] = dataProvider.showEmpty
        }
        it.target = this
      }

      setDataProvider(dataProvider)
      this.expand(dataProvider.root.last().children(dataProvider.showEmpty))

      width = "100%"
      isHeightByRows = true
    })

    return parent
  }
}

object Actions {
  private val ACTIONS = mapOf<TreeObjectType, (TreeNode) -> EditableLayout>(
      TreeObjectType.POSITIVE to ::PositiveActions,
      TreeObjectType.POSITIVE_AND_INFINITE to ::PositiveAndInfiniteActions,
      TreeObjectType.FREE to ::FreeActions,
      TreeObjectType.DAMAGE to ::DamageActions,
      TreeObjectType.ARMOR to ::ArmorActions,
      TreeObjectType.IMPORTANCE to ::ImportanceActions,
      TreeObjectType.LINE to ::LineActions,
      TreeObjectType.MULTILINE to ::MultilineActions,
      TreeObjectType.FEATURE to { _ -> EditableLayout() },
      TreeObjectType.SINGLE_REF to ::RefActions,
      TreeObjectType.MULTIPLE_REF to ::RefActions
  )

  operator fun invoke(node: TreeNode): EditableLayout = when (node) {
    is TerminalTreeObjectAttributeNode -> ACTIONS[node.type]!!(node)
    is NonTerminalTreeObjectAttributeNode -> ACTIONS[node.type]!!(node)
    is TreeObjectNode -> when (node.obj.type) {
      TreeObjectType.FEATURE_OBJECT -> LineActions(node)
      TreeObjectType.FEATURE_DATA -> FeatureDataActions(node)
      else -> throw IllegalArgumentException()
    }
    else -> EditableLayout()
  }
}

open class EditableLayout : HorizontalLayout() {
  lateinit var locator: ServiceLocator
  var editing: Boolean = false
    set(new) {
      changeEditing(new)
    }
  lateinit var refreshItem: (TreeNode, Boolean) -> Unit

  init {
    isSpacing = false
    isPadding = false
  }

  open fun changeEditing(new: Boolean) {}
}

class PositiveActions(private val item: TreeNode) : EditableLayout() {
  private val staticLabel: Label
  private val editField: TextField

  init {
    val initial = (item.value()[0]?.let { it as BigDecimal } ?: BigDecimal.ZERO).stripTrailingZeros().toPlainString()

    staticLabel = Label(initial)
    editField = TextField().apply {
      isVisible = false
      value = initial
      width = "6em"

      addValueChangeListener {
        val new = it.value.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO
        item.setValue(0, new)
        item.value()[0] = new
        staticLabel.text = new.stripTrailingZeros().toPlainString()
      }
    }
    add(staticLabel, editField)
  }

  override fun changeEditing(new: Boolean) {
    staticLabel.isVisible = !new
    editField.isVisible = new
  }
}

class PositiveAndInfiniteActions(private val item: TreeNode) : EditableLayout() {
  private val staticLabel: Label
  private val editField: TextField
  private val editInfinite: Checkbox

  init {
    val initial = (item.value()[0]?.let { it as BigDecimal } ?: BigDecimal.ZERO)
    staticLabel = Label(positiveWithInfiniteLabel(initial))

    val (field, infinite) = valueWithInfinite(initial) {
      item.setValue(0, it)
      item.value()[0] = it
      staticLabel.text = positiveWithInfiniteLabel(it)
    }
    editField = field
    editInfinite = infinite
    add(staticLabel, field, infinite)
    setVerticalComponentAlignment(FlexComponent.Alignment.END, field, infinite)
  }

  override fun changeEditing(new: Boolean) {
    staticLabel.isVisible = !new
    editField.isVisible = new
    editInfinite.isVisible = new
  }
}

class FreeActions(private val item: TreeNode) : EditableLayout() {
  private val staticLabel: Label
  private val editField: TextField

  init {
    val initial = (item.value()[0]?.let { it as BigDecimal } ?: BigDecimal.ZERO).stripTrailingZeros().toPlainString()

    staticLabel = Label(initial)
    editField = TextField().apply {
      isVisible = false
      value = initial
      width = "6em"

      addValueChangeListener {
        val new = it.value.toBigDecimalOrNull() ?: BigDecimal.ZERO
        item.setValue(0, new)
        item.value()[0] = new
        staticLabel.text = new.stripTrailingZeros().toPlainString()
      }
    }
    add(staticLabel, editField)
  }

  override fun changeEditing(new: Boolean) {
    staticLabel.isVisible = !new
    editField.isVisible = new
  }
}

class DamageActions(private val item: TreeNode) : EditableLayout() {
  private val staticLabel: Label
  private val damage: TextField
  private val slash: Label
  private val destruction: TextField

  init {
    var damageValue = (item.value()[0]?.let { it as BigDecimal }
        ?: BigDecimal.ZERO).stripTrailingZeros().toPlainString()
    var destructionValue = (item.value()[1]?.let { it as BigDecimal }
        ?: BigDecimal.ZERO).stripTrailingZeros().toPlainString()
    staticLabel = Label(damageValue + "/" + destructionValue)

    damage = TextField().apply {
      isVisible = false
      value = damageValue
      width = "6em"
      addValueChangeListener {
        val new = it.value.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO
        item.setValue(0, new)
        item.value()[0] = new

        damageValue = new.stripTrailingZeros().toPlainString()
        staticLabel.text = damageValue + "/" + destructionValue
      }
    }
    slash = Label("/").apply {
      isVisible = false
    }
    destruction = TextField().apply {
      isVisible = false
      value = destructionValue
      width = "6em"
      addValueChangeListener {
        val new = it.value.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO
        item.setValue(1, new)
        item.value()[1] = new

        destructionValue = new.stripTrailingZeros().toPlainString()
        staticLabel.text = damageValue + "/" + destructionValue
      }
    }
    add(staticLabel, damage, slash, destruction)
    setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, damage, slash, destruction)
  }

  override fun changeEditing(new: Boolean) {
    staticLabel.isVisible = !new
    damage.isVisible = new
    slash.isVisible = new
    destruction.isVisible = new
  }
}

class ArmorActions(private val item: TreeNode) : EditableLayout() {
  private val staticLabel: Label

  private val strongField: TextField
  private val strongInfinite: Checkbox
  private val slashOne: Label
  private val standardField: TextField
  private val standardInfinite: Checkbox
  private val slashTwo: Label
  private val weakField: TextField
  private val weakInfinite: Checkbox

  init {
    var strongValue = item.value()[0]?.let { it as BigDecimal } ?: BigDecimal.ZERO
    var standardValue = item.value()[1]?.let { it as BigDecimal } ?: BigDecimal.ZERO
    var weakValue = item.value()[2]?.let { it as BigDecimal } ?: BigDecimal.ZERO

    staticLabel = Label(positiveWithInfiniteLabel(strongValue) + "/" + positiveWithInfiniteLabel(standardValue) + "/" + positiveWithInfiniteLabel(weakValue))

    val (field, infinite) = valueWithInfinite(strongValue) {
      item.setValue(0, it)
      item.value()[0] = it

      strongValue = it
      staticLabel.text = positiveWithInfiniteLabel(strongValue) + "/" + positiveWithInfiniteLabel(standardValue) + "/" + positiveWithInfiniteLabel(weakValue)
    }
    strongField = field
    strongInfinite = infinite
    slashOne = Label("/").apply {
      isVisible = false
    }
    val (fieldA, infiniteA) = valueWithInfinite(item.value()[1]?.let { it as BigDecimal } ?: BigDecimal.ZERO) {
      item.setValue(1, it)
      item.value()[1] = it

      standardValue = it
      staticLabel.text = positiveWithInfiniteLabel(strongValue) + "/" + positiveWithInfiniteLabel(standardValue) + "/" + positiveWithInfiniteLabel(weakValue)
    }
    standardField = fieldA
    standardInfinite = infiniteA
    slashTwo = Label("/").apply {
      isVisible = false
    }
    val (fieldB, infiniteB) = valueWithInfinite(item.value()[2]?.let { it as BigDecimal } ?: BigDecimal.ZERO) {
      item.setValue(2, it)
      item.value()[2] = it

      weakValue = it
      staticLabel.text = positiveWithInfiniteLabel(strongValue) + "/" + positiveWithInfiniteLabel(standardValue) + "/" + positiveWithInfiniteLabel(weakValue)
    }
    weakField = fieldB
    weakInfinite = infiniteB

    add(staticLabel, field, infinite, slashOne, fieldA, infiniteA, slashTwo, fieldB, infiniteB)
    setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, field, infinite, slashOne, fieldA, infiniteA, slashTwo, fieldB, infiniteB)
  }

  override fun changeEditing(new: Boolean) {
    staticLabel.isVisible = !new
    strongField.isVisible = new
    strongInfinite.isVisible = new
    slashOne.isVisible = new
    standardField.isVisible = new
    standardInfinite.isVisible = new
    slashTwo.isVisible = new
    weakField.isVisible = new
    weakInfinite.isVisible = new
  }
}

class ImportanceActions(private val item: TreeNode) : EditableLayout() {
  private val staticLabel: Label
  private val edit: ComboBox<Int>

  init {
    val initial = (item.value()[0]?.let { it as BigDecimal } ?: BigDecimal.ZERO).toInt()

    staticLabel = Label(NumberOption.IMPORTANCE_OPTIONS[initial])
    edit = ComboBox<Int>().apply {
      isVisible = false

      setItems((0..9).toList())
      setItemLabelGenerator { NumberOption.IMPORTANCE_OPTIONS[it] }
      this.value = initial
      addValueChangeListener {
        val new = it.value.toBigDecimal()
        item.setValue(0, new)
        item.value()[0] = new

        staticLabel.text = NumberOption.IMPORTANCE_OPTIONS[it.value]
      }
    }

    add(staticLabel, edit)
  }

  override fun changeEditing(new: Boolean) {
    staticLabel.isVisible = !new
    edit.isVisible = new
  }
}

class LineActions(private val item: TreeNode) : EditableLayout() {
  private val staticLabel: Label
  private val editField: TextField

  init {
    val initial = (item.value()[0]?.let { it as String } ?: "")

    staticLabel = Label(initial)
    editField = TextField().apply {
      isVisible = false
      value = initial

      addValueChangeListener {
        val new = it.value
        item.setValue(0, new)
        item.value()[0] = new
        staticLabel.text = new
      }
    }
    add(staticLabel, editField)
  }

  override fun changeEditing(new: Boolean) {
    staticLabel.isVisible = !new
    editField.isVisible = new
  }
}

class MultilineActions(private val item: TreeNode) : EditableLayout() {
  private val staticLabel: Label
  private val editField: TextArea

  init {
    val initial = (item.value()[0]?.let { it as String } ?: "")

    staticLabel = Label(initial)
    editField = TextArea().apply {
      isVisible = false
      value = initial

      addValueChangeListener {
        val new = it.value
        item.setValue(0, new)
        item.value()[0] = new
        staticLabel.text = new
      }
    }
    add(staticLabel, editField)
  }

  override fun changeEditing(new: Boolean) {
    staticLabel.isVisible = !new
    editField.isVisible = new
  }
}

class RefActions(private val item: TreeNode) : EditableLayout() {
  private val addButton: Button
  private val createButton: Button

  init {
    addButton = Button(Icon(VaadinIcon.PLUS)) {
      TreeObjectOptionSelection(locator.treeObjectDictionary, item.dictionary(), locator.settings.map { it.id }, null) {
        item.add(it)
        refreshItem(item, true)
      }.open()
    }
    addButton.isVisible = false

    createButton = Button(Icon(VaadinIcon.MAGIC)) {
      val new = locator.treeObjectDictionary.create(item.dictionary(), locator.setting.id)
      item.add(new)
      refreshItem(item, true)
    }
    createButton.isVisible = false

    add(addButton, createButton)
  }

  override fun changeEditing(new: Boolean) {
    addButton.isVisible = new
    createButton.isVisible = new && item.canCreate()
  }
}

class FeatureDataActions(private val item: TreeNode) : EditableLayout() {
  private val removeButton: Button

  init {
    removeButton = Button(Icon(VaadinIcon.CLOSE)) {
      item.parent!!.remove(item)
      refreshItem(item.parent, true)
    }

    removeButton.isVisible = false
    add(removeButton)
  }

  override fun changeEditing(new: Boolean) {
    removeButton.isVisible = new
  }
}

private fun positiveWithInfiniteLabel(value: BigDecimal): String =
    if (value == Int.MAX_VALUE.toBigDecimal()) "Бесконечность" else value.stripTrailingZeros().toPlainString()

private fun valueWithInfinite(value: BigDecimal, setter: (BigDecimal) -> Unit): Pair<TextField, Checkbox> {
  val isInfinite = value == Int.MAX_VALUE.toBigDecimal()

  val field = TextField().apply {
    isVisible = false
    width = "6em"

    this.value = if (isInfinite) "" else value.stripTrailingZeros().toPlainString()
    isEnabled = !isInfinite

    addValueChangeListener {
      setter(it.value.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO)
    }

    width = "10em"
  }
  val inifinite = Checkbox("Бесконечность").apply {
    isVisible = false

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

class TreeObjectOptionSelection(
    private val treeObjectDictionary: TreeObjectDictionary,
    private val dictionary: String,
    private val settings: List<Long>,
    private val initialValue: Long?,
    private val onSelect: (TreeObjectOption) -> Unit
) : Dialog() {
  init {
    add(VerticalLayout().apply {
      val dataProvider = treeObjectDictionary.dataProvider(dictionary, settings)

      val filter = TextField().apply {
        addValueChangeListener {
          dataProvider.filter = it.value
        }
      }

      val grid = Grid<TreeObjectOption>().apply {
        addColumn { it.name }
        addColumn { it.rate }

        setItems(dataProvider)

        addThemeVariants(GridVariant.LUMO_NO_BORDER)
      }

      add(filter)
      add(grid)
      add(HorizontalLayout().apply {
        add(Button("Принять") {
          grid
              .selectedItems
              .singleOrNull()
              ?.let { onSelect(it) }
          close()
        })
        add(Button("Закрыть") { close() })
      })


      width = "100%"
      height = "100%"
      isPadding = false
      isSpacing = false
    })

    width = "100%"
    height = "100%"
  }
}