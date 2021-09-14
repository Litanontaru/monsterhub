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
import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.FeatureDataDataProvider
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.form.EditDialog

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
                  setItems(locator.finderData(type.featureType) as DataProvider<SettingObject, String>)
                  setItemLabelGenerator { it.name }
                  isVisible = false
                }

                val addButton = Button(Icon(VaadinIcon.PLUS)) {
                  addNew.optionalValue.ifPresent {
                    update(obj) {
                      val newFeatureData = FeatureData().apply { feature = it as Feature }
                      update(newFeatureData) {}
                      obj.features.add(newFeatureData)
                    }

                    updateOneFeaturePanel()
                  }
                }.apply {
                  addThemeVariants(ButtonVariant.LUMO_SMALL)
                  isVisible = false
                }

                add(label, addNew, addButton)
                setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)

                addClickListener {
                  addNew.isVisible = !addNew.isVisible
                  addButton.isVisible = !addButton.isVisible
                }
              }
            } else {
              HorizontalLayout().apply {
                val label = Label(type.name + ": " + existing.display())

                val editButton = Button(Icon(VaadinIcon.EDIT)) {
                  EditDialog(existing, locator) {
                    update(existing) {}
                    label.text = existing.display()
                  }.open()
                }.apply {
                  addThemeVariants(ButtonVariant.LUMO_SMALL)
                }
                val closeButton = Button(Icon(VaadinIcon.CLOSE_SMALL)) {
                  update(obj) { obj.features.remove(existing) }
                  update(existing) { existing.deleteOnly = true }

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
              setItems(locator.finderData(type.featureType) as DataProvider<SettingObject, String>)
              setItemLabelGenerator { it.name }
              isVisible = false
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
              isVisible = false
            }

            add(label, addNew, addButton)
            setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)

            addClickListener {
              addNew.isVisible = !addNew.isVisible
              addButton.isVisible = !addButton.isVisible
            }
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
    }

    return parent
  }
}