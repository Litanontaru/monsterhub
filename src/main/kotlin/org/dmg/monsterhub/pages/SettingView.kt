package org.dmg.monsterhub.pages

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Div
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
import org.dmg.monsterhub.pages.edit.data.ObjectFinderDataProviderForSetting
import org.dmg.monsterhub.pages.edit.data.ObjectFinderDataProviderService
import org.dmg.monsterhub.pages.edit.data.ObjectFinderDataProviderService.Companion.getRecursive
import org.dmg.monsterhub.pages.edit.form.ChangeDialog
import org.dmg.monsterhub.pages.edit.form.EditPanel
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.repository.FeatureContainerItemRepository
import org.dmg.monsterhub.repository.FeatureDataDesignationRepository
import org.dmg.monsterhub.repository.WeaponAttackRepository
import org.dmg.monsterhub.repository.WeaponRepository
import org.dmg.monsterhub.service.FeatureContainerServiceLocator
import org.dmg.monsterhub.service.FeatureDataRepository
import org.dmg.monsterhub.service.SettingService


@Route("setting/:settingId/edit")
class SettingView(
    private val settingService: SettingService,
    private val objectDataProviderService: ObjectTreeDataProviderService,
    private val objectFinderDataProviderService: ObjectFinderDataProviderService,
    private val featureDataRepository: FeatureDataRepository,
    private val featureContainerItemRepository: FeatureContainerItemRepository,
    private val featureDataDesignationRepository: FeatureDataDesignationRepository,
    private val weaponAttackRepository: WeaponAttackRepository,
    private val weaponRepository: WeaponRepository,
    private val featureContainerServiceLocator: FeatureContainerServiceLocator
) : Div(), BeforeEnterObserver, HasDynamicTitle {
  lateinit var setting: Setting
  lateinit var data: ObjectTreeDataProvider
  lateinit var finderData: ObjectFinderDataProviderForSetting

  override fun beforeEnter(event: BeforeEnterEvent?) {
    if (event != null) {
      set(event.routeParameters["settingId"].get().toLong())
    }
  }

  private fun set(settingId: Long) {
    setting = settingService.get(settingId)
    val settings = getRecursive(setting).toList()

    data = objectDataProviderService(setting)
    finderData = objectFinderDataProviderService(settings)

    var edit: EditPanel? = null
    val rightPanel = VerticalLayout().apply {
      height = "100%"
      width = "100%"
      isPadding = false
      isSpacing = false
    }

    val leftPanel = VerticalLayout().apply {
      fun click(item: SettingObject) {
        if (edit != null) {
          rightPanel.remove(edit)
        }
        edit = EditPanel(
            item,
            ServiceLocator(
                settings,

                data,
                finderData,
                featureDataRepository,
                featureContainerItemRepository,
                featureDataDesignationRepository,
                weaponRepository,
                weaponAttackRepository,
                featureContainerServiceLocator
            )
        )

        rightPanel.add(edit)
      }

      val tree = TreeGrid<SettingObject>().also { tree ->
        tree.addComponentHierarchyColumn { obj ->
          val item: Component = when (obj) {
            is Folder -> HorizontalLayout().apply {
              add(Icon(VaadinIcon.FOLDER_O))
              add(Label(obj.name))

              width = "100%"
              isPadding = false
            }
            else -> Label(obj.name)
          }

          item.apply { contextMenu(obj) }
        }
        tree.contextMenu(null)

        tree.setSelectionMode(Grid.SelectionMode.SINGLE);

        tree.addItemClickListener { it.item?.let { click(it) } }

        tree.setDataProvider(data)
        data.onAdd = {
          tree.select(it)
          click(it)
        }

        tree.addThemeVariants(GridVariant.LUMO_COMPACT)
      }


      add(tree)

      height = "100%"
      width = "30%"
      isPadding = false
      isSpacing = false
    }

    val apply = HorizontalLayout().apply {
      add(leftPanel, rightPanel)

      setSizeFull()
      isPadding = true
    }
    add(apply)

    height = "100%"
    width = "100%"
  }

  private fun Component.contextMenu(obj: SettingObject?) {
    ContextMenu().also {
      if (obj == null || obj is Folder) {
        val toAdd = it.addItem("Добавить")
        data.dataProviders().forEach { dataProvider ->
          toAdd.subMenu.addItem(dataProvider.name) {
            ChangeDialog("Создать", "") {
              data.add(dataProvider.create().apply {
                name = it
                if (obj is Folder) parent = obj
              })
            }.open()
          }
        }
      }

      if (obj != null) {
        if (obj is Folder) {
          it.addItem("Переименовать") {
            ChangeDialog("Новое название", obj.name) {
              obj.name = it
              data.update(obj)
            }.open()
          }
        }

        it.addItem("Удалить") {
          data.delete(obj)
        }
      }

      it.target = this
    }
  }

  override fun getPageTitle(): String = "MonsterHub. ${setting.name}"
}

