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
      addSelectionListener {
        val oldSelected = selectedItem
        selectedItem = it.firstSelectedItem.orElse(null)
        if (oldSelected != null) {
          dataProvider.refreshItem(oldSelected)
        }
        if (selectedItem != null) {
          dataProvider.refreshItem(selectedItem)
        }
      }

      //DOUBLE CLICK
      addItemDoubleClickListener {
        editItem(it.item, locator, update, dataProvider)
      }

      //NAME
      addComponentHierarchyColumn { item ->
        Label(item.compactedName()).also { label ->
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

      //ACTIONS
      addComponentColumn { item ->
        HorizontalLayout().apply {
          isVisible = item == selectedItem

          lateinit var updateVisibility: () -> Unit
          val components = mutableListOf<Component>()

          //ADD
          val addNewComboBox = ComboBox<SettingObject>().apply { setItemLabelGenerator { it.name } }

          val add = Button(Icon(VaadinIcon.PLUS)) {
            addNewComboBox.optionalValue.ifPresent {
              val new = FeatureData().apply { feature = it as Feature }
              item.last().add(new) { update(it) { } }

              dataProvider.refreshItem(item, true)
              updateVisibility()

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
                .data
                .dataProviders()
                .first { it.supportType(item.last().addableType()!!) }
                .create()
                .let { update(it) { it.hidden = true } as Feature }
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
              addNewComboBox.setItems(locator.finderData(last.addableType()!!) as DataProvider<SettingObject, String>)
            }

            add.isVisible = last.canAdd()
            edit.isVisible = last.canEdit()
            delete.isVisible = last.canRemove()
            create.isVisible = last.canCreate()
          }
          updateVisibility = ::setVisibility

          setVisibility()

          components.add(addNewComboBox)
          components.add(add)
          components.add(edit)
          components.add(delete)
          components.add(create)

          isPadding = false
          isMargin = false

          add(*components.toTypedArray())
          setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, *components.toTypedArray())
        }
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