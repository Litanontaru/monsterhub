package org.dmg.monsterhub.pages

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.treegrid.TreeGrid

class SettingObjectTree(
    data: ObjectTreeDataProvider2
) : VerticalLayout() {
  init {
    val tree = TreeGrid<SettingObjectTreeNode>()
    tree.addComponentHierarchyColumn { obj ->
      try {
        val element: Component = when {
          obj.isFolder() -> HorizontalLayout().apply {
            add(Icon(VaadinIcon.FOLDER_O))
            add(Label(obj.folderName()))

            width = "100%"
            isPadding = false
          }
          else -> Label(obj.name)
        }
        element.apply { contextMenu(obj) }
      } catch (e: Exception) {
        Label("ERROR")
      }
    }
    tree.contextMenu(null)
    tree.setSelectionMode(Grid.SelectionMode.SINGLE)
    tree.addItemClickListener {
      it.item?.let {
        clickAndHistory(it)
        Unit
      }
    }
    tree.setDataProvider(data)

    add(tree)

    height = "100%"
    width = "30%"
    isPadding = false
    isSpacing = false
  }

  private fun clickAndHistory(it: SettingObjectTreeNode) {

  }

  private fun Component.contextMenu(obj: SettingObjectTreeNode?) {

  }
}