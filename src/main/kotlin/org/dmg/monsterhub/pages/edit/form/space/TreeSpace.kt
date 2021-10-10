package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.provider.DataProvider
import org.dmg.monsterhub.data.ContainerData
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.SelectionTable
import org.dmg.monsterhub.pages.edit.data.*
import org.dmg.monsterhub.pages.edit.form.EditDialog

object TreeSpace : Space {
  override fun support(obj: Any): Boolean = obj is ContainerData

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val parent = mutableListOf<Component>()
    val obj = anyObj as ContainerData

    val dataProvider = AtomicTreeNodeDataProvider(obj.toTree(null))

    parent.add(TreeGrid<AtomicTreeNode>().apply {
      //ADD SELECTION TRACKER
      var selectedItem: AtomicTreeNode? = null
      fun updateSelection(node: AtomicTreeNode?) {
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

      //DOUBLE CLICK
      addItemDoubleClickListener {
        editItem(it.item, locator, update, dataProvider)
      }

      //NAME and ACTIONS
      addComponentHierarchyColumn { item ->
        HorizontalLayout().apply {
          val name = Label(item.compactedName()).also { label ->
            val actions = mutableListOf<Pair<String, () -> Unit>>()

            if (item.last() != item && item.compactable) {
              actions += "Раскрыть" to {
                item.compactable = false
                dataProvider.refreshItem(item, true)
              }
            }
            if (!item.compactable && !item.isStopper && item.children.size == 1) {
              actions += "Схлопнуть" to {
                item.compactable = true
                dataProvider.refreshItem(item, true)
              }
            }

            item
                .compacted()
                .find { it.canEdit() && it.editableObject() is Power }
                ?.let { powerNode ->
                  actions += "Добавить Приобретение" to {
                    powerNode.parent?.let {
                      val power = powerNode.editableObject() as Power
                      val wrapped = PowerService.wrapWithAcquisition(power, locator, update)
                      val featureData = it.editableObject() as FeatureData
                      update(featureData) { featureData.feature = wrapped }

                      var node = it
                      while (node.last() == it) node = node.parent ?: break
                      dataProvider.refreshItem(node, true)
                    } ?: Unit
                  }
                }

            if (actions.isNotEmpty()) {
              ContextMenu().also { menu ->
                actions.forEach { (text, action) -> menu.addItem(text) { action() } }

                menu.target = label
              }
            }

            this.addClickListener { updateSelection(item) }
          }

          val actions = HorizontalLayout().apply {
            isVisible = item == selectedItem

            lateinit var updateVisibility: () -> Unit
            val components = mutableListOf<Component>()

            //ADD
            val addNewComboBox = ComboBox<SettingObject>().apply {
              setItemLabelGenerator { it.name }
            }

            fun addSelected(settingObject: SettingObject) {
              val new = FeatureData().apply { feature = settingObject as Feature }
              item.last().add(new) { update(it) { } }

              dataProvider.refreshItem(item, true)
              updateVisibility()
            }

            val selectFromTable = Button(Icon(VaadinIcon.GRID_SMALL)) {
              SelectionTable(item.last().addableType()!!, locator) {
                addSelected(it)
              }.open()
            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
            }

            val add = Button(Icon(VaadinIcon.PLUS)) {
              addNewComboBox.optionalValue.ifPresent {
                addSelected(it)

                addNewComboBox.value = null
              }
            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
            }

            //EDIT
            val edit = Button(Icon(VaadinIcon.EDIT)) {
              editItem(item, locator, update, dataProvider)
            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
            }

            //DELETE
            val delete = Button(Icon(VaadinIcon.CLOSE_SMALL)) {
              item.last().remove { update(it) {} }

              dataProvider.refreshItem(item, true)
              if (item.parent?.parent != null) {
                item.parent!!.let { dataProvider.refreshItem(it, true) }
              } else {
                dataProvider.refreshAll()
              }

              updateVisibility()
            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
            }

            //CERATE
            val create = Button(Icon(VaadinIcon.MAGIC)) {
              val new = locator
                  .objectManagerService
                  .create(item.last().addableType()!!)
                  .also { it.hidden = true }
                  .let { update(it) { } as Feature }
                  .let { created -> FeatureData().apply { feature = created } }

              item.last().add(new) { update(it) { } }

              dataProvider.refreshItem(item, true)
              item.parent?.let { dataProvider.refreshItem(it) } ?: run { dataProvider.refreshAll() }

              updateVisibility()
            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
            }

            fun setVisibility() {
              val last = item.last()
              addNewComboBox.isVisible = last.canAdd()
              if (last.canAdd()) {
                addNewComboBox.setItems(locator.data.find(last.addableType()!!) as DataProvider<SettingObject, String>)
              }

              add.isVisible = last.canAdd()
              selectFromTable.isVisible = last.canAdd()
              edit.isVisible = last.canEdit()
              delete.isVisible = last.canRemove()
              create.isVisible = last.canCreate()
            }
            updateVisibility = ::setVisibility

            setVisibility()

            components.add(addNewComboBox)
            components.add(add)
            components.add(selectFromTable)
            components.add(edit)
            components.add(delete)
            components.add(create)

            isPadding = false
            isMargin = false

            add(*components.toTypedArray())
            setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, *components.toTypedArray())
          }

          add(name, actions)
          setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, name, actions)
        }
      }.also {
        it.isAutoWidth = true
      }

      //RATE
      addColumn {
        it.compactRate()?.takeIf { it.isNotBlank() }
      }.also {
        it.width = "6em"
        it.flexGrow = 0
      }

      setDataProvider(dataProvider)

      this.expand(dataProvider.root.last().children)

      width = "100%"
      isHeightByRows = true
    })

    return parent
  }

  private fun editItem(item: AtomicTreeNode, locator: ServiceLocator, update: (Any, () -> Unit) -> Any, dataProvider: AtomicTreeNodeDataProvider) {
    if (item.last().canEdit()) {
      EditDialog(item.last().editableObject(), locator) {
        update(item.last().editableObject()) { }
        dataProvider.refreshItem(item)
        item.parent?.let { dataProvider.refreshItem(it) }
      }.open()
    }
  }
}