package org.dmg.monsterhub.pages.edit.form

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField

class ChangeDialog(
    title: String,
    value: String,
    onSet: (String) -> Unit
) : Dialog() {
  init {
    add(VerticalLayout().apply {
      val field = TextField(title).apply {
        this.value = value
        isAutofocus = true
      }

      add(field)
      add(HorizontalLayout().apply {
        add(Button("Принять") {
          onSet(field.value)
          close()
        })
        add(Button("Закрыть") { close() })
      })

      isPadding = false
      isSpacing = false
    })
  }
}