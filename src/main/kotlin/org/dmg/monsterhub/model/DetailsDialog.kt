package org.dmg.monsterhub.model

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea

class DetailsDialog(detailed: Detailed) : Dialog() {
  init {
    add(VerticalLayout().apply {
      val text = TextArea().apply {
        value = detailed.details

        width = "100%"
        height = "100%"
      }
      add(text)
      add(HorizontalLayout().apply {
        add(Button("ОК") {
          detailed.details = text.value
          close()
        })
        add(Button("Закрыть") { close() })

        width = "100%"
        isPadding = false
      })

      width = "100%"
      height = "100%"
      isPadding = false
      isSpacing = false
    })

    width = "40%"
  }
}