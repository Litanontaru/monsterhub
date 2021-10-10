package org.dmg.monsterhub.pages

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.DataProvider
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

class SelectionTable(
    private val featureType: String,
    private val locator: ServiceLocator,
    private val onSelect: (SettingObject) -> Unit
) : Dialog() {
  init {
    add(VerticalLayout().apply {
      val dataProvider = locator.data.select(featureType)

      val grid = Grid<SettingObject>().apply {
        addColumn { it.name }
        addColumn {
          when (it) {
            is Feature -> it.rate ?: ""
            else -> ""
          }
        }
        setItems(dataProvider as DataProvider<SettingObject, Void>)

        addThemeVariants(GridVariant.LUMO_NO_BORDER)
      }

      add(grid)
      add(HorizontalLayout().apply {
        add(Button("Принять") {
          grid
              .selectedItems
              .singleOrNull()
              ?.let { onSelect(it) }
          close()
        })
        add(Button("Закрыть") { close() })
      })


      width = "100%"
      height = "100%"
      isPadding = false
      isSpacing = false
    })

    width = "100%"
    height = "100%"
  }
}