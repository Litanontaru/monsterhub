package org.dmg.monsterhub.model

import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.textfield.TextField


class DoubleField(
    val get: () -> Double,
    val set: (Double) -> Unit,
    val error: String
) : TextField() {
  init {
    value = get().toString()
    addValueChangeListener {
      try {
        set(java.lang.Double.parseDouble(it.value))
      } catch (e: NumberFormatException) {
        Notification(error).apply { duration = 1000 }.open()
      }
    }

    width = "4em"
  }
}