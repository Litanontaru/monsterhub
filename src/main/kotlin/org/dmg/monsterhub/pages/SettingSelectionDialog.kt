package org.dmg.monsterhub.pages

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider
import com.vaadin.flow.data.provider.Query
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.repository.SettingRepository
import java.util.stream.Stream

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

class SettingBackEndDataProvider(
    val repository: SettingRepository
) : AbstractBackEndDataProvider<Setting, String>() {
  override fun sizeInBackEnd(query: Query<Setting, String>?) =
      query
          ?.filter
          ?.orElse(null)
          ?.let {
            query.page
            query.pageSize
            when {
              it.isNotBlank() -> repository.countByNameContaining(it)
              else -> 0
            }
          }
          ?: 0

  override fun fetchFromBackEnd(query: Query<Setting, String>?) =
      query
          ?.filter
          ?.orElse(null)
          ?.let {
            query.page
            query.pageSize
            when {
              it.isNotBlank() -> repository.findAllByNameContaining(it).stream()
              else -> Stream.empty()
            }
          }
          ?: Stream.empty<Setting>()
}