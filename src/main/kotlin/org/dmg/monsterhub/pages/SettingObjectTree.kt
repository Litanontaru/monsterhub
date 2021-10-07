package org.dmg.monsterhub.pages

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.treegrid.TreeGrid
import org.dmg.monsterhub.pages.edit.form.ChangeDialog
import org.dmg.monsterhub.service.SettingObjectDataProvider

class SettingObjectTree(
    private val data: ObjectTreeDataProvider2,
    private val dataProviders: List<SettingObjectDataProvider>,
    onClick: (SettingObjectTreeNode) -> Unit
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
        onClick(it)
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

  private fun Component.contextMenu(obj: SettingObjectTreeNode?) {
    ContextMenu().also {
      if (obj == null || obj.isFolder()) {
        val toAdd = it.addItem("Добавить")
        dataProviders
            .sortedBy { it.name }
            .filter { it.canCreate() }
            .forEach { dataProvider ->
              toAdd.subMenu.addItem(dataProvider.name) {
                ChangeDialog("Создать", "") {
                  val new = dataProvider.create().apply {
                    name = it
                    folder = obj?.name ?: ""
                    setting = data.setting!!
                  }
                  dataProvider.save(new)
                }.open()
              }
            }
      }

      if (obj != null) {
        if (obj.isFolder()) {
          //todo
        } else {
          it.addItem("Удалить") {
            data.hide(obj)
          }
        }

        it.addItem("Переместить") {
          ChangeDialog("Переместить в", obj.folder) { changedFolder ->
            val newFolder = if (changedFolder.endsWith('.') || changedFolder.isBlank()) {
              changedFolder
            } else {
              changedFolder + "."
            }
            data.move(obj, newFolder)
          }.open()
        }

        /*it.addItem("Переместить в игровой мир") {
          SettingSelectionDialog(settingRepository) {
            val violations = dependencyAnalyzer.analyzeMoveToSetting(obj, it)
            if (violations.isNotEmpty()) {
              Notification("${violations.size} нарушений видимости найдено").apply {
                duration = 3000
              }.open()
            } else {
              val oldParent = obj.parent

              obj.setting = it
              obj.parent = null
              data.update(obj)

              if (oldParent == null) {
                data.refreshAll()
              } else {
                data.refreshItem(oldParent, true)
              }
            }
          }.open()
        }*/
      }

      it.target = this
    }
  }
}