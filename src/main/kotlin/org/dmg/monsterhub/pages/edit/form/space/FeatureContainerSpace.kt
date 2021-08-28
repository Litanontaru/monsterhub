package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.DataProvider
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.pages.edit.data.FeatureContainerItemDataProvider
import org.dmg.monsterhub.pages.edit.form.FeatureContaiterItemEditDialog
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

fun HasComponents.featureContainerSpace(obj: FeatureContainer, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
  val dataProvider = FeatureContainerItemDataProvider(
      obj,
      { update(it) {} }
  )

  add(HorizontalLayout().apply {
    val label = Label("Дополнительные свойства")

    val addNew = TextField()

    val addButton = Button(Icon(VaadinIcon.PLUS)) {
      addNew.optionalValue.ifPresent {
        val newFeatureContainerItem = FeatureContainerItem().apply { featureType = addNew.value }
        locator.featureContainerItemRepository.save(newFeatureContainerItem)
        dataProvider.add(newFeatureContainerItem)
        addNew.value = ""
      }
    }.apply {
      addThemeVariants(ButtonVariant.LUMO_SMALL)
    }

    add(label, addNew, addButton)
    setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)
  })

  val grid = Grid<FeatureContainerItem>().apply {
    fun edit(containerItem: FeatureContainerItem) {
      FeatureContaiterItemEditDialog(containerItem) {
        locator.featureContainerItemRepository.save(it)
        dataProvider.refreshItem(it)
      }.open()
    }

    addItemDoubleClickListener { edit(it.item) }

    addColumn { it.featureType }
    addColumn { it.name }
    addColumn { it.onlyOne }

    addComponentColumn { containerItem ->
      HorizontalLayout().apply {
        add(Button(Icon(VaadinIcon.EDIT)) {
          edit(containerItem)
        }.apply {
          addThemeVariants(ButtonVariant.LUMO_SMALL)
        })

        add(Button(Icon(VaadinIcon.CLOSE_SMALL)) {
          dataProvider.delete(containerItem)
        }.apply {
          addThemeVariants(ButtonVariant.LUMO_SMALL)
        })

        isPadding = false
      }

    }
    setItems(dataProvider as DataProvider<FeatureContainerItem, Void>)

    width = "100%"
    isHeightByRows = true
  }

  add(grid)
}