package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.treegrid.TreeGrid
import org.dmg.monsterhub.api.TreeObjectDictionary
import org.dmg.monsterhub.api.TreeObjectOption
import org.dmg.monsterhub.data.ContainerData
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.data.tree2.TreeDataProvider
import org.dmg.monsterhub.pages.edit.data.tree2.TreeNode
import org.dmg.monsterhub.pages.edit.data.tree2.TreeObjectNode
import org.dmg.monsterhub.pages.edit.form.space2.Lines

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
        Lines.toComponent(item, item == selectedItem, locator) { node, refreshChildren ->
          dataProvider.refreshItem(node, refreshChildren)
        }
      }.also {
        it.width = "100%"
        it.flexGrow = 1
        it.isAutoWidth = true
      }
      addColumn {
        it.compacted().toList().reversed().mapNotNull { it.rate() }.find { it.isNotBlank() }
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

class TreeObjectOptionSelection(
  private val treeObjectDictionary: TreeObjectDictionary,
  private val dictionary: List<String>,
  private val settings: List<Long>,
  private val initialValue: Long?,
  private val onSelect: (TreeObjectOption) -> Unit
) : Dialog() {
  init {
    add(VerticalLayout().apply {
      val dataProvider = treeObjectDictionary.dataProvider(dictionary, settings)

      val filter = TextField().apply {
        width = "100%"

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