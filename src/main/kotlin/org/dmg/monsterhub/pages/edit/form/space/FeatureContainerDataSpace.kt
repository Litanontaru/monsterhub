package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.DataProvider
import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.FeatureDataDataProvider
import org.dmg.monsterhub.pages.edit.form.EditDialog
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object FeatureContainerDataSpace : Space {
  override fun support(obj: Any) = obj is FeatureContainerData && obj !is Power

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit): List<Component> {
    val parent = mutableListOf<Component>()
    val obj = anyObj as FeatureContainerData

    val meta = locator.featureContainerServiceLocator.containerMeta(obj)
    if (meta != null) {
      meta.containFeatureTypes.forEach { type ->
        if (type.onlyOne) {
          val place = VerticalLayout().apply {
            width = "100%"
            isPadding = false
            isSpacing = false
          }
          parent.add(place)

          var featurePanel: Component? = null
          fun updateOneFeaturePanel() {
            if (featurePanel != null) {
              place.remove(featurePanel)
            }

            val existing = obj.features.find { it.feature.featureType == type.featureType }
            featurePanel = if (existing == null) {
              HorizontalLayout().apply {
                val label = Label(type.name)

                val addNew = ComboBox<SettingObject>().apply {
                  setItems(locator.fiderData(type.featureType) as DataProvider<SettingObject, String>)
                  setItemLabelGenerator { it.name }
                }

                val addButton = Button(Icon(VaadinIcon.PLUS)) {
                  addNew.optionalValue.ifPresent {
                    val newFeatureData = FeatureData().apply { feature = it as Feature }
                    locator.featureDataRepository.save(newFeatureData)
                    obj.features.add(newFeatureData)
                    update(obj) {}

                    updateOneFeaturePanel()
                  }
                }.apply {
                  addThemeVariants(ButtonVariant.LUMO_SMALL)
                }

                add(label, addNew, addButton)
                setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)
              }
            } else {
              HorizontalLayout().apply {
                val label = Label(type.name + ": " + existing.display())

                val editButton = Button(Icon(VaadinIcon.EDIT)) {
                  EditDialog(existing, locator) {
                    locator.featureDataRepository.save(existing)
                    label.text = existing.display()
                  }.open()
                }.apply {
                  addThemeVariants(ButtonVariant.LUMO_SMALL)
                }
                val closeButton = Button(Icon(VaadinIcon.CLOSE_SMALL)) {
                  update(obj) { obj.features.remove(existing) }
                  locator.featureDataRepository.delete(existing)

                  updateOneFeaturePanel()
                }.apply {
                  addThemeVariants(ButtonVariant.LUMO_SMALL)
                }

                add(label, editButton, closeButton)
                setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, editButton, closeButton)
              }
            }
            place.add(featurePanel)
          }

          updateOneFeaturePanel()
        } else {
          val dataProvider = FeatureDataDataProvider(
              type.featureType,
              obj,
              { update(it) {} }
          )

          parent.add(HorizontalLayout().apply {
            val label = Label(type.name)

            val addNew = ComboBox<SettingObject>().apply {
              setItems(locator.fiderData(type.featureType) as DataProvider<SettingObject, String>)
              setItemLabelGenerator { it.name }
            }

            val addButton = Button(Icon(VaadinIcon.PLUS)) {
              addNew.optionalValue.ifPresent {
                val newFeatureData = FeatureData().apply { feature = it as Feature }
                locator.featureDataRepository.save(newFeatureData)
                dataProvider.add(newFeatureData)
                addNew.value = null
              }
            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL)
            }

            add(label, addNew, addButton)
            setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)
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
          }

          parent.add(grid)
        }
      }
    }

    return parent
  }
}