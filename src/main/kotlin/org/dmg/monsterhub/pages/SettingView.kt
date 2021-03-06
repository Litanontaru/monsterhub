package org.dmg.monsterhub.pages

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.contextmenu.MenuItem
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.menubar.MenuBar
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.provider.hierarchy.HierarchicalConfigurableFilterDataProvider
import com.vaadin.flow.router.*
import org.dmg.monsterhub.api.TreeObjectController
import org.dmg.monsterhub.api.TreeObjectDictionary
import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.ObjectFinderDataProviderService
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.form.ChangeDialog
import org.dmg.monsterhub.pages.edit.form.EditPanel
import org.dmg.monsterhub.pages.edit.form.EditPanelConfig
import org.dmg.monsterhub.repository.*
import org.dmg.monsterhub.service.*


@Route("setting/:settingId/edit/:objId")
class SettingView(
    private val settingService: SettingService,
    private val objectDataProviderService: ObjectTreeDataProviderService,
    private val objectFinderDataProviderService: ObjectFinderDataProviderService,
    private val featureDataRepository: FeatureDataRepository,
    private val featureContainerItemRepository: FeatureContainerItemRepository,
    private val featureDataDesignationRepository: FeatureDataDesignationRepository,
    private val powerEffectRepository: PowerEffectRepository,
    private val weaponAttackRepository: WeaponAttackRepository,
    private val weaponRepository: WeaponRepository,
    private val settingRepository: SettingRepository,
    private val freeFeatureDataProvider: FreeFeatureDataProvider,
    private val dependencyAnalyzer: DependencyAnalyzer,
    private val transactionService: TransactionService,
    private val treeObjectController: TreeObjectController,
    private val treeObjectDictionary: TreeObjectDictionary
) : Div(), BeforeEnterObserver, HasDynamicTitle {
  private val config: EditPanelConfig = EditPanelConfig("????????????????????????")

  private var initialized = false
  private lateinit var setting: Setting
  private lateinit var data: ObjectTreeDataProvider
  private lateinit var dataWithFilter: HierarchicalConfigurableFilterDataProvider<SettingObject, Void, SettingObjectTreeFilter>

  private lateinit var tree: TreeGrid<SettingObject>
  private var selectedObjId: Long? = null
  private lateinit var rightPanel: VerticalLayout

  override fun beforeEnter(event: BeforeEnterEvent?) {
    if (event != null) {
      set(event.routeParameters["settingId"].get().toLong())
      event.routeParameters["objId"].ifPresent { select(it.toLong()) }
    }
  }

  private fun set(settingId: Long) {
    if (!initialized || settingId != setting.id) {
      initialized = true

      setting = settingService.get(settingId)

      data = objectDataProviderService(setting)
      dataWithFilter = data.withConfigurableFilter()

      rightPanel = VerticalLayout().apply {
        height = "100%"
        width = "100%"
        isPadding = false
        isSpacing = false
      }

      val leftPanel = VerticalLayout().apply {
        val filter = TextField().apply {
          addValueChangeListener {
            dataWithFilter.setFilter(SettingObjectTreeFilter(filterValue = it.value))
          }
          width = "100%"
        }

        val menuBar = MenuBar()
        val menuItem = menuBar.addItem(Icon(VaadinIcon.MENU))
        menuItem.subMenu.addItem("?????????????? ?????????????? ??????") {
          ChangeDialog("???????????????? ???????????????? ????????", "??????") { newName ->
            Setting()
                .apply { name = newName }
                .let { settingRepository.save(it) }
                .also { it.setting = it }
                .let { settingRepository.save(it) }
                .let { goToSetting(it) }
          }.open()
        }
        menuItem.subMenu.addItem("?????????????? ?????????????? ??????") {
          SettingSelectionDialog(settingRepository) { goToSetting(it) }.open()
        }
        val top = HorizontalLayout(filter, menuBar).apply { width = "100%" }

        tree = TreeGrid<SettingObject>().also { tree ->
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

          tree.setSelectionMode(Grid.SelectionMode.SINGLE)

          tree.addItemClickListener {
            it.item?.let {
              clickAndHistory(it)
              Unit
            }
          }

          tree.setDataProvider(dataWithFilter)

          data.onAdd = {
            tree.select(it)
            clickAndHistory(it)
          }

          tree.addThemeVariants(GridVariant.LUMO_COMPACT)
        }


        add(top, tree)

        height = "100%"
        width = "30%"
        isPadding = false
        isSpacing = false
      }

      removeAll()
      add(HorizontalLayout().apply {
        add(leftPanel, rightPanel)

        setSizeFull()
        isPadding = true
      })

      height = "100%"
      width = "100%"
    }
  }

  private fun goToSetting(newSetting: Setting) {
    UI.getCurrent().navigate(
        SettingView::class.java,
        RouteParameters(
            RouteParam("settingId", newSetting.id.toString()),
            RouteParam("objId", newSetting.id.toString())
        )
    )
  }

  private fun clickAndHistory(it: SettingObject) {
    click(it)
    history(it)
  }

  private fun click(item: SettingObject) {
    rightPanel.removeAll()

    rightPanel.add(EditPanel(
        item,
        ServiceLocator(
            setting,

            objectFinderDataProviderService,
            data,
            featureDataRepository,
            featureContainerItemRepository,
            featureDataDesignationRepository,
            powerEffectRepository,
            weaponRepository,
            weaponAttackRepository,
            settingRepository,
            freeFeatureDataProvider,
            transactionService,
            treeObjectController,
            treeObjectDictionary,

            config
        ).also { it.refreshSettings() }
    ))
  }

  private fun history(item: SettingObject) {
    val routeConfiguration = RouteConfiguration.forSessionScope()
    val parameters = RouteParameters(mutableMapOf(
        "settingId" to setting.id.toString(),
        "objId" to item.id.toString()
    ))
    val url = routeConfiguration.getUrl(SettingView::class.java, parameters)
    UI.getCurrent().page.history.pushState(null, url)
  }

  private fun select(objId: Long) {
    if (selectedObjId == null || selectedObjId != objId) {
      data
          .find(objId)
          ?.also { obj ->
            generateSequence(obj.parent) { it.parent }
                .toList()
                .reversed()
                .forEach { tree.expand(it) }

            selectedObjId = objId
            tree.select(obj)
            click(obj)
          }
    }
  }

  private fun Component.contextMenu(obj: SettingObject?) {
    fun addCreateMenu(menuItem: MenuItem, factory: SettingObjectFactory) {
      menuItem.subMenu.addItem(factory.name) {
        ChangeDialog("??????????????", "") {
          data.add(factory.create().apply {
            this.name = it
            if (obj is Folder) parent = obj
          })
        }.open()
      }
    }

    ContextMenu().also {
      if (obj == null || obj is Folder) {
        val toAdd = it.addItem("????????????????")
        data
            .dataProviders()
            .flatMap {
              when {
                it.groupFactories().isBlank() -> it.factories().asSequence().map { it.name to listOf(it) }
                else -> sequenceOf(it.groupFactories() to it.factories().sortedBy { it.name })
              }
            }
            .sortedBy { it.first }
            .forEach { (name, factories) ->
              if (factories.size == 1) {
                addCreateMenu(toAdd, factories[0])
              } else {
                val menuItem = toAdd.subMenu.addItem(name)
                factories.forEach { addCreateMenu(menuItem, it) }
              }
            }
      }

      if (obj != null) {
        if (obj is Folder) {
          it.addItem("??????????????????????????") {
            ChangeDialog("?????????? ????????????????", obj.name) {
              obj.name = it
              data.update(obj)
            }.open()
          }
        }

        it.addItem("??????????????") {
          data.delete(obj)
        }

        it.addItem("??????????????????????") {
          val initial = generateSequence(obj.parent) { it.parent }
              .map { it.name }
              .toList()
              .reversed()
              .joinToString(".")

          ChangeDialog("?????????????????????? ??", initial) { changedFolder ->
            val folders = changedFolder.split("\\.".toRegex()).filter { it.isNotBlank() }
            if (folders.isEmpty()) {
              data.move(obj, null)
            } else {
              var parent: Folder? = null
              for (folder in folders) {
                val next = data.firstFolder(parent, folder)
                parent = next
                if (parent == null) {
                  break
                }
              }
              if (parent != null) {
                data.move(obj, parent)
              }
            }
          }.open()
        }

        it.addItem("????????????????") {
          data.reread(obj)
        }

        it.addItem("?????????????????????? ?? ?????????????? ??????") {
          SettingSelectionDialog(settingRepository) {
            val violations = dependencyAnalyzer.analyzeMoveToSetting(obj, it)
            if (violations.isNotEmpty()) {
              Notification("${violations.size} ?????????????????? ?????????????????? ??????????????").apply {
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
        }

        it.addItem("?????????? ??????????????????????????") {
          dataWithFilter.setFilter(SettingObjectTreeFilter(findUsages = obj))
        }
      }

      it.target = this
    }
  }

  override fun getPageTitle(): String = "MonsterHub. ${setting.name}"
}

