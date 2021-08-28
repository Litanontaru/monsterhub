package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.HasComponents
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
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.CreatureHierarchyDataProvider
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object CreatureSpace : Space {
  override fun support(obj: Any) = obj is Creature

  override fun use(parent: HasComponents, anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    val dataProvider = CreatureHierarchyDataProvider(
        anyObj as Creature,
        { update(it) {} }
    )

    parent.add(HorizontalLayout().apply {
      val label = Label("Основа")

      val addNew = ComboBox<SettingObject>().apply {
        setItems(locator.fiderData("CREATURE") as DataProvider<SettingObject, String>)
        setItemLabelGenerator { it.name }
      }

      val addButton = Button(Icon(VaadinIcon.PLUS)) {
        addNew.optionalValue.ifPresent {
          dataProvider.add(it as Creature)
          addNew.value = null
        }
      }.apply {
        addThemeVariants(ButtonVariant.LUMO_SMALL)
      }

      add(label, addNew, addButton)
      setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)
    })
    val grid = Grid<Creature>().apply {
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
      setItems(dataProvider as DataProvider<Creature, Void>)

      width = "100%"
      isHeightByRows = true
    }
    parent.add(grid)
  }
}