package org.dmg.monsterhub.pages

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.contextmenu.MenuItem
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.menubar.MenuBar
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.treegrid.TreeGrid
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.pages.edit.form.ChangeDialog
import org.dmg.monsterhub.repository.SettingRepository
import org.dmg.monsterhub.service.DependencyAnalyzer
import org.dmg.monsterhub.service.SettingObjectDataProvider
import org.dmg.monsterhub.service.SettingObjectFactory

class SettingObjectTree(
    private val data: ObjectTreeDataProvider2,
    private val dataProviders: List<SettingObjectDataProvider>,
    private val settingRepository: SettingRepository,
    private val dependencyAnalyzer: DependencyAnalyzer,

    onClick: (SettingObjectTreeNode) -> Unit
) : VerticalLayout() {
  init {
    val filter = TextField().apply {
      addValueChangeListener { data.filter = it.value }
      width = "100%"
    }

    val menuBar = MenuBar()
    val menuItem = menuBar.addItem(Icon(VaadinIcon.MENU))
    menuItem.subMenu.addItem("Создать игровой мир") {
      ChangeDialog("Название игрового мира", "Мир") { newName ->
        Setting()
            .apply { name = newName }
            .let { settingRepository.save(it) }
            .also { it.setting = it }
            .let { settingRepository.save(it) }
            .let {
              data.setting = it
              onClick(SettingTreeNode(it.id, it.name))
            }
      }.open()
    }
    menuItem.subMenu.addItem("Выбрать игровой мир") {
      SettingSelectionDialog(settingRepository) {
        data.setting = it
        onClick(SettingTreeNode(it.id, it.name))
      }.open()
    }
    val top = HorizontalLayout(filter, menuBar).apply { width = "100%" }

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

    add(top, tree)

    height = "100%"
    width = "30%"
    isPadding = false
    isSpacing = false
  }

  private fun Component.contextMenu(obj: SettingObjectTreeNode?) {
    fun addCreateMenu(menuItem: MenuItem, factory: SettingObjectFactory, dataProvider: SettingObjectDataProvider) {
      menuItem.subMenu.addItem(factory.name) {
        ChangeDialog("Создать", "") {
          val new = factory.create().apply {
            name = it
            folder = obj?.name ?: ""
            setting = data.setting!!
          }
          dataProvider.save(new)
        }.open()
      }
    }


    ContextMenu().also {
      if (obj == null || obj.isFolder()) {
        val toAdd = it.addItem("Добавить")
        dataProviders
            .flatMap {
              when {
                it.groupFactories().isBlank() -> it.factories().map { factory -> factory.name to listOf(factory) to it }
                else -> listOf(it.groupFactories() to it.factories().sortedBy { it.name } to it)
              }
            }
            .sortedBy { it.first.first }
            .forEach { (pair, dataProvider) ->
              val (name, factories) = pair
              if (factories.size == 1) {
                addCreateMenu(toAdd, factories[0], dataProvider)
              } else {
                val menuItem = toAdd.subMenu.addItem(name)
                factories.forEach { addCreateMenu(menuItem, it, dataProvider) }
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

        it.addItem("Переместить в игровой мир") {
          SettingSelectionDialog(settingRepository) {
            dataProviders
                .find { it.supportType(obj.featureType) }
                ?.getById(obj.id)
                ?.let { settingObject ->
                  val violations = dependencyAnalyzer.analyzeMoveToSetting(settingObject, it)
                  if (violations.isNotEmpty()) {
                    Notification("${violations.size} нарушений видимости найдено").apply {
                      duration = 3000
                    }.open()
                  } else {
                    data.move(obj, it)
                  }
                }
          }.open()
        }
      }

      it.target = this
    }
  }
}