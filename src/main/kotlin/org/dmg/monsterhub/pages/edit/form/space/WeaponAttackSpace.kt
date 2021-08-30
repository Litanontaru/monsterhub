package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.provider.DataProvider
import org.dmg.monsterhub.data.Weapon
import org.dmg.monsterhub.data.WeaponAttack
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.data.WeaponAttackDataProvider
import org.dmg.monsterhub.pages.edit.form.EditDialog

object WeaponAttackSpace : Space {
  override fun support(obj: Any) = obj is Weapon

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit): List<Component> {
    val parent = mutableListOf<Component>()

    val obj = anyObj as Weapon
    val dataProvider = WeaponAttackDataProvider(obj) { update(it) {} }

    parent.add(HorizontalLayout().apply {
      val label = Label("Атака")

      val addButton = Button(Icon(VaadinIcon.PLUS)) {
        dataProvider.add(WeaponAttack())
      }.apply {
        addThemeVariants(ButtonVariant.LUMO_SMALL)
      }

      add(label, addButton)
      setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addButton)
    })

    val grid = Grid<WeaponAttack>().apply {
      var selectedItem: WeaponAttack? = null
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

      addColumn { it.display() }

      addComponentColumn { item ->

        HorizontalLayout().apply {
          isVisible = item == selectedItem

          add(Button(Icon(VaadinIcon.EDIT)) {
            EditDialog(item, locator) {
              dataProvider.modify(item)
            }.open()
          }.apply {
            addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
          })

          add(Button(Icon(VaadinIcon.CLOSE)) {
            dataProvider.delete(item)
          }.apply {
            addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON)
          })
        }
      }
      setItems(dataProvider as DataProvider<WeaponAttack, Void>)

      width = "100%"
      isHeightByRows = true
    }
    parent.add(grid)

    return parent
  }
}