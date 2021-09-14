package org.dmg.monsterhub.pages.edit.form

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.meta.FeatureContainerItem

class FeatureContaiterItemEditDialog(
    obj: FeatureContainerItem,
    save: (FeatureContainerItem) -> Unit
) : Dialog() {
  init {
    add(VerticalLayout(
        Label(obj.featureType),
        TextField("Название").apply {
          value = obj.name

          addValueChangeListener {
            obj.name = it.value
            save(obj)
          }
        },
        Checkbox("Только одно").apply {
          value = obj.onlyOne
          addValueChangeListener {
            obj.onlyOne = it.value
            save(obj)
          }
        },
        Checkbox("Можно встраивать").apply {
          value = obj.allowHidden
          addValueChangeListener {
            obj.allowHidden = it.value
            save(obj)
          }
        },
        Button(Icon(VaadinIcon.CLOSE)) {
          close()
        }
    ).apply {
      isPadding = false
      isSpacing = false
    })
  }
}