package org.dmg.monsterhub.pages.edit.form

import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.FreeFeature
import org.dmg.monsterhub.service.FreeFeatureDataProvider

fun HasComponents.freeFeatureSpace(obj: FreeFeature, update: (Any, () -> Unit) -> Unit) {
  add(HorizontalLayout().apply {
    add(ComboBox<String>("Тип").apply {
      setItems(FreeFeatureDataProvider.MY_TYPES)

      value = obj.featureType
      addValueChangeListener {
        update(obj) { obj.featureType = it.value }
      }
      width = "100%"
    })

    add(TextField("Показатель").apply {
      value = obj.rate ?: ""
      addValueChangeListener {
        update(obj) { obj.rate = it.value.takeIf { it.isNotBlank() } }
      }
    })

    width = "100%"
  })
}