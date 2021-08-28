package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.provider.DataProvider
import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.FeatureContainerVo
import org.dmg.monsterhub.pages.edit.data.PowerTreeDataProvider
import org.dmg.monsterhub.pages.edit.form.EditDialog
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object PowerTreeSpace : Space {
  override fun support(obj: Any): Boolean = obj is Power

  override fun use(parent: HasComponents, anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    powerTreeSpace(parent, anyObj as FeatureContainerData, locator, update)
  }
}

fun powerTreeSpace(parent: HasComponents, obj: FeatureContainerData, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
  val meta = locator.featureContainerServiceLocator.containerMeta(obj)
  if (meta != null) {
    val dataProvider = PowerTreeDataProvider(obj, meta)
    parent.add(TreeGrid<FeatureContainerVo>().apply {
      var selectedItem: FeatureContainerVo? = null
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

      addHierarchyColumn { it.name }.apply {
        isAutoWidth = true
      }
      addColumn { it.rate }.apply {
        isAutoWidth = true
      }
      addComponentColumn { item ->

        HorizontalLayout().apply {
          isVisible = item == selectedItem

          val components = mutableListOf<Component>()

          if (item.canAdd) {
            val addNew = ComboBox<SettingObject>().apply {
              setItems(locator.fiderData(item.featureType) as DataProvider<SettingObject, String>)
              setItemLabelGenerator { it.name }
            }

            components.add(addNew)
            components.add(Button(Icon(VaadinIcon.PLUS)) {
              addNew.optionalValue.ifPresent {
                update(obj) {
                  update(item) {
                    val new = FeatureData().apply { feature = it as Feature }
                    locator.featureDataRepository.save(new)
                    item.add(new)
                  }
                }
                dataProvider.refreshItem(item, true)
                item.parent?.let { dataProvider.refreshItem(it) }

                addNew.value = null
              }
            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
            })
          }
          if (item.canEdit) {

            components.add(Button(Icon(VaadinIcon.EDIT)) {
              EditDialog(item.featureData!!, locator) {
                update(obj) {
                  locator.featureDataRepository.save(item.featureData!!)
                  dataProvider.refreshItem(item)
                  item.parent?.let { dataProvider.refreshItem(it) }
                }
              }.open()
            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
            })

            components.add(Button(Icon(VaadinIcon.CLOSE_SMALL)) {
              update(obj) {
                item.delete()?.let {
                  update(item) {}
                  dataProvider.refreshItem(it, true)
                } ?: run {
                  update(item) {}
                  dataProvider.refreshAll()
                }
              }
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
  }
}
