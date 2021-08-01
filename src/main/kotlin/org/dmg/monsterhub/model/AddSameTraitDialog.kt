package org.dmg.monsterhub.model

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class AddSameTraitDialog(
        val trait: String,
        val group: String?,
        val okAction: () -> Unit) : Dialog() {
    init {
        add(VerticalLayout().apply {
            val message = if (group == null) "" else " с группой $group"

            add(Label("Черта $trait$message уже есть. Заменить?"))

            add(HorizontalLayout().apply {
                add(Button("Да") {
                    okAction()
                    close()
                })
                add(Button("Нет") { close() })

                isPadding = false
            })

            isPadding = false
        })
    }
}