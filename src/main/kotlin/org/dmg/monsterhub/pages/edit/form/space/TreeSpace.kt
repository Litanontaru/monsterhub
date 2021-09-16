package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.provider.DataProvider
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.AtomicTreeNode
import org.dmg.monsterhub.pages.edit.data.AtomicTreeNodeDataProvider
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.data.toTree
import org.dmg.monsterhub.pages.edit.form.EditDialog

object TreeSpace : Space {
  override fun support(obj: Any): Boolean = obj is Power

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val parent = mutableListOf<Component>()
    val obj = anyObj as Power

    val dataProvider = AtomicTreeNodeDataProvider(obj.toTree(null))


    parent.add(TreeGrid<AtomicTreeNode>().apply {
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

      addHierarchyColumn { it.compactedName() }.also {
        it.isAutoWidth = true
      }
      addColumn { it.last().rate()?.takeIf { it.isNotBlank() } }.also {
        it.width = "6em"
        it.flexGrow = 0
      }
      addComponentColumn { item ->

        HorizontalLayout().apply {
          isVisible = item == selectedItem

          val components = mutableListOf<Component>()

          val last = item.last()
          if (last.canAdd()) {
            val addNew = ComboBox<SettingObject>().apply {
              setItems(locator.finderData(last.addableType()!!) as DataProvider<SettingObject, String>)
              setItemLabelGenerator { it.name }
            }

            components.add(addNew)
            components.add(Button(Icon(VaadinIcon.PLUS)) {
              addNew.optionalValue.ifPresent {
                val new = FeatureData().apply { feature = it as Feature }
                last.add(new) { update(it) { } }

                dataProvider.refreshItem(item, true)

                addNew.value = null
              }
            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
            })
          }

          if (last.canEdit()) {
            components.add(Button(Icon(VaadinIcon.EDIT)) {
              EditDialog(last.editableObject(), locator) {
                update(last.editableObject()) { }
                dataProvider.refreshItem(item)
                item.parent?.let { dataProvider.refreshItem(it) }
              }.open()
            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
            })
          }

          if (last.canRemove()) {
            components.add(Button(Icon(VaadinIcon.CLOSE_SMALL)) {
              last.remove { update(it) {} }

              item.parent!!.let { dataProvider.refreshItem(it, true) }
            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
            })
          }

          if (last.canCreate()) {
            components.add(Button(Icon(VaadinIcon.MAGIC)) {
              val new = locator
                  .data
                  .dataProviders()
                  .first { it.supportType(last.addableType()!!) }
                  .create()
                  .let { update(it) { it.hidden = true } as Feature }
                  .let { created -> FeatureData().apply { feature = created } }

              last.add(new) { update(it) { } }

              dataProvider.refreshItem(item, true)
              item.parent?.let { dataProvider.refreshItem(it) } ?: run { dataProvider.refreshAll() }


            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
            })
          }

          isPadding = false
          isMargin = false

          add(*components.toTypedArray())
          setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, *components.toTypedArray())
        }
      }

      setDataProvider(dataProvider)

      width = "100%"
      isHeightByRows = true
    })

    return parent
  }
}