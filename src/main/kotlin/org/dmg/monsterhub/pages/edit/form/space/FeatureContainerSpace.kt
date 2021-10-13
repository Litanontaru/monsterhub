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
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.DataProvider
import org.dmg.monsterhub.data.ContainerData
import org.dmg.monsterhub.data.Faction
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.pages.edit.data.FeatureContainerItemDataProvider
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.form.FeatureContaiterItemEditDialog
import org.dmg.monsterhub.repository.update

object FeatureContainerSpace : Space {
  override fun support(obj: Any) = obj is FeatureContainer && obj !is ContainerData && obj !is Faction

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val parent = mutableListOf<Component>()

    val dataProvider = FeatureContainerItemDataProvider(
        anyObj as FeatureContainer,
        { update(it) {} }
    )
    parent.add(HorizontalLayout().apply {
      val label = Label("Дополнительные свойства")

      val addNew = TextField()

      val addButton = Button(Icon(VaadinIcon.PLUS)) {
        addNew.optionalValue.ifPresent {
          FeatureContainerItem()
              .apply { featureType = addNew.value }
              .also { locator.featureContainerItemRepository.update(it).let { dataProvider.add(it) } }
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
          locator.featureContainerItemRepository.update(it).let { dataProvider.refreshItem(it) }
        }.open()
      }

      addItemDoubleClickListener { edit(it.item) }

      addColumn { it.featureType }
      addColumn { it.name }
      addColumn { it.onlyOne }
      addColumn { it.allowHidden }

      addComponentColumn { containerItem ->
        HorizontalLayout().apply {
          add(Button(Icon(VaadinIcon.EDIT)) {
            edit(containerItem)
            dataProvider.refreshItem(containerItem)
          }.apply {
            addThemeVariants(ButtonVariant.LUMO_SMALL)
          })

          add(Button(Icon(VaadinIcon.CLOSE_SMALL)) {
            dataProvider.delete(containerItem)
            containerItem.deleteOnly = true
            locator.featureContainerItemRepository.update(containerItem)
            dataProvider.refreshAll()
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
    parent.add(grid)

    return parent
  }
}
