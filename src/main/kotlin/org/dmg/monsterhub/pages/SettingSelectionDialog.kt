package org.dmg.monsterhub.pages

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.pages.edit.data.SettingBackEndDataProvider
import org.dmg.monsterhub.repository.SettingRepository

class SettingSelectionDialog(
    repository: SettingRepository,
    onSet: (Setting) -> Unit
) : Dialog() {
  init {
    add(VerticalLayout().apply {
      val selection = ComboBox<Setting>("Игровой мир").apply {
        setItems(SettingBackEndDataProvider(repository))
        setItemLabelGenerator { it.name }
      }

      add(selection)
      add(HorizontalLayout().apply {
        add(Button("Принять") {
          onSet(selection.value)
          close()
        })
        add(Button("Закрыть") { close() })
      })

      isPadding = false
      isSpacing = false
    })
  }
}

