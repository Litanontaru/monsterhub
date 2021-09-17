package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.provider.DataProvider
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.data.SettingBackEndDataProvider
import org.dmg.monsterhub.pages.edit.data.SettingHierarchyDataProvider

object SettingBaseSpace : Space {
  override fun support(obj: Any) = obj is Setting

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val obj = anyObj as Setting

    val parent = mutableListOf<Component>()

    val dataProvider = SettingHierarchyDataProvider(obj) {
      update(it) {}
      locator.refreshSettings()
    }

    parent.add(HorizontalLayout().apply {
      val label = Label("Основа")

      val addNew = ComboBox<Setting>().apply {
        setItems(SettingBackEndDataProvider(locator.settingRepository))
        setItemLabelGenerator { it.name }
      }

      val addButton = Button(Icon(VaadinIcon.PLUS)) {
        addNew.optionalValue.ifPresent {
          dataProvider.add(it)
          addNew.value = null
        }
      }.apply {
        addThemeVariants(ButtonVariant.LUMO_SMALL)
      }

      add(label, addNew, addButton)
      setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)
    })
    val grid = Grid<Setting>().apply {
      addColumn { it.name }
      addComponentColumn { base ->
        HorizontalLayout().apply {
          add(Button(Icon(VaadinIcon.CLOSE_SMALL)) {
            dataProvider.delete(base)
          }.apply {
            addThemeVariants(ButtonVariant.LUMO_SMALL)
          })

          isPadding = false
        }
      }
      setItems(dataProvider as DataProvider<Setting, Void>)

      width = "100%"
      isHeightByRows = true
    }
    parent.add(grid)

    return parent
  }
}