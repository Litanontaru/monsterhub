package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.DataProvider
import org.dmg.monsterhub.data.ContainerData
import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.FeatureDataDataProvider
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.form.EditDialog

object FeatureContainerDataSpace : Space {
  override fun support(obj: Any) = obj is FeatureContainerData && obj !is ContainerData

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val parent = mutableListOf<Component>()
    val obj = anyObj as FeatureContainerData


    val meta = obj.meta()
    val componentsByType = mutableMapOf<String, List<Component>>()
    fun updateActionComponents(selectedType: String?) {
      componentsByType.forEach { featureType, components ->
        components.forEach { it.isVisible = featureType == selectedType }
      }
    }

    meta.forEach { type ->
      if (type.onlyOne) {
        fun findExisting() = obj.features.find { it.feature.featureType == type.featureType }

        val existingLabel = Label()

        val addSpace = HorizontalLayout()
        val editSpace = HorizontalLayout()

        fun updateExistingData() {
          val existing = findExisting()
          if (existing == null) {
            addSpace.isVisible = true
            editSpace.isVisible = false
          } else {
            addSpace.isVisible = false
            editSpace.isVisible = true

            existingLabel.text = type.name + ": " + existing.display()
          }
        }

        val addNew = ComboBox<SettingObject>().apply {
          setItems(locator.data.find(type.featureType) as DataProvider<SettingObject, String>)
          setItemLabelGenerator { it.name }
        }

        val addButton = Button(Icon(VaadinIcon.PLUS)) {
          addNew.optionalValue.ifPresent {
            update(obj) {
              val newFeatureData = FeatureData().apply { feature = it as Feature }
              update(newFeatureData) {}
              obj.features.add(newFeatureData)
            }

            updateExistingData()
          }
        }.apply {
          addThemeVariants(ButtonVariant.LUMO_SMALL)
        }

        val editButton = Button(Icon(VaadinIcon.EDIT)) {
          val existing = findExisting()!!
          EditDialog(existing, locator) {
            update(existing) {}
            existingLabel.text = existing.display()
          }.open()
        }.apply {
          addThemeVariants(ButtonVariant.LUMO_SMALL)
        }

        val closeButton = Button(Icon(VaadinIcon.CLOSE_SMALL)) {
          val existing = findExisting()!!
          update(obj) { obj.features.remove(existing) }
          update(existing) { existing.deleteOnly = true }

          updateExistingData()
        }.apply {
          addThemeVariants(ButtonVariant.LUMO_SMALL)
        }

        componentsByType.put(type.featureType, listOf(addNew, addButton, editButton, closeButton))

        addSpace.apply {
          val label = Label(type.name)

          add(label, addNew, addButton)
          setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)

          addClickListener { updateActionComponents(type.featureType) }
        }

        editSpace.apply {
          add(existingLabel, editButton, closeButton)
          setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, existingLabel, editButton, closeButton)

          addClickListener { updateActionComponents(type.featureType) }
        }

        updateExistingData()


        parent.add(VerticalLayout().apply {
          add(addSpace, editSpace)

          width = "100%"
          isPadding = false
          isSpacing = false
        })
      } else {
        val dataProvider = FeatureDataDataProvider(
            type.featureType,
            obj
        ) { update(it) {} }

        parent.add(HorizontalLayout().apply {
          val label = Label(type.name)

          val addNew = ComboBox<SettingObject>().apply {
            setItems(locator.data.find(type.featureType) as DataProvider<SettingObject, String>)
            setItemLabelGenerator { it.name }
          }

          val addButton = Button(Icon(VaadinIcon.PLUS)) {
            addNew.optionalValue.ifPresent {
              val newFeatureData = FeatureData().apply { feature = it as Feature }
              update(newFeatureData) {}

              dataProvider.add(newFeatureData)
              addNew.value = null
            }
          }.apply {
            addThemeVariants(ButtonVariant.LUMO_SMALL)
          }

          componentsByType.put(type.featureType, listOf(addNew, addButton))

          add(label, addNew, addButton)
          setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)

          addClickListener { updateActionComponents(type.featureType) }
        })

        val grid = Grid<FeatureData>().apply {
          fun edit(item: FeatureData) {
            EditDialog(item, locator) {
              dataProvider.update(item)
            }.open()
          }

          addItemDoubleClickListener { edit(it.item) }

          addColumn { it.display() }.isAutoWidth = true

          addComponentColumn { featureData ->
            HorizontalLayout().apply {
              add(Button(Icon(VaadinIcon.EDIT)) {
                edit(featureData)
              }.apply {
                addThemeVariants(ButtonVariant.LUMO_SMALL)
              })

              add(Button(Icon(VaadinIcon.CLOSE_SMALL)) {
                update(featureData) { featureData.deleteOnly = true }

                dataProvider.delete(featureData)
              }.apply {
                addThemeVariants(ButtonVariant.LUMO_SMALL)
              })

              isPadding = false
            }
          }

          setItems(dataProvider as DataProvider<FeatureData, Void>)

          width = "100%"
          isHeightByRows = true
          addThemeVariants(GridVariant.LUMO_NO_BORDER)
        }

        parent.add(grid)
      }
    }

    updateActionComponents(null)

    return parent
  }
}