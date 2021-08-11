package org.dmg.monsterhub.pages

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.service.SettingService
import com.vaadin.flow.component.grid.dnd.GridDropMode


@Route("setting/:settingId/edit")
class SettingView(
    val settingService: SettingService,
    val objectDataProviderService: ObjectTreeDataProviderService
) : HorizontalLayout(), BeforeEnterObserver, HasDynamicTitle {
  lateinit var setting: Setting
  lateinit var data: ObjectTreeDataProvider

  override fun beforeEnter(event: BeforeEnterEvent?) {
    if (event != null) {
      set(event.routeParameters["settingId"].get().toLong())
    }
  }

  private fun set(settingId: Long) {
    setting = settingService.get(settingId)
    data = objectDataProviderService(setting)

    add(VerticalLayout().apply {
      add(HorizontalLayout().apply {
        add(Button(Icon(VaadinIcon.FOLDER_ADD)) {
          data.add(Folder().apply {
            name = "Папка"
          })
        })
        add(Button(Icon(VaadinIcon.PLUS)) {

        })

        width = "100%"
        isPadding = false
      })

      val tree = TreeGrid<SettingObject>().also { tree ->
        var draggedItem: SettingObject? = null

        tree.addComponentHierarchyColumn { obj ->
          Label(obj.name).apply {
            ContextMenu().also {
              if (obj is Folder) {
                it.addItem("Добавить") {
                  data.add(Folder().apply {
                    name = "Папка"
                    parent = obj
                  })
                }
              }
              it.addItem("Удалить") {
                  data.delete(obj)
              }

              it.target = this
            }
          }
        }

        tree.setSelectionMode(Grid.SelectionMode.NONE);
        tree.isRowsDraggable = true;

        tree.addDragStartListener {
          draggedItem = it.getDraggedItems().get(0)
          tree.dropMode = GridDropMode.BETWEEN
        }
        tree.addDragEndListener {
          draggedItem = null
          tree.dropMode = null
        }
        tree.addDropListener {
          val new: SettingObject = it.dropTargetItem.get()
          draggedItem?.let {
            if (new != it && new is Folder) {
              data.move(it, new)
            }
          }
        }

        tree.setDataProvider(data)
      }


      add(tree)

      isPadding = false
      isSpacing = false
    })
  }

  override fun getPageTitle(): String = "MonsterHub. ${setting.name}"
}

