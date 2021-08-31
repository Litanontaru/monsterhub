package org.dmg.monsterhub.pages.edit.form

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

class EditDialog(
    private val obj: Any,
    private val locator: ServiceLocator,
    private val onUpdate: (() -> Unit)? = null
): Dialog() {
  init {
    add(VerticalLayout().apply {
      add(EditPanel(
          obj,
          locator,
          onUpdate
      ))
      add(Button(Icon(VaadinIcon.CLOSE)) {
        close()
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