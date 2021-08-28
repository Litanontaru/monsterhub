package org.dmg.monsterhub.pages.edit.form

import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.meta.NumberOption

fun HasComponents.featureSpace(obj: Feature, update: (Any, () -> Unit) -> Unit) {
  add(TextArea("Описание").apply {
    value = obj.description
    addValueChangeListener {
      update(obj) { obj.description = it.value }
    }
    width = "100%"
  })

  add(HorizontalLayout().apply {
    add(ComboBox<String>("X").apply {
      setItems(NumberOption.display)
      value = obj.x.displayName
      addValueChangeListener {
        update(obj) { obj.x = NumberOption(it.value) }
      }
      width = "100%"
    })

    add(ComboBox<String>("Y").apply {
      setItems(NumberOption.display)
      value = obj.y.displayName
      addValueChangeListener {
        update(obj) { obj.y = NumberOption(it.value) }
      }
      width = "100%"
    })

    add(ComboBox<String>("Z").apply {
      setItems(NumberOption.display)
      value = obj.z.displayName
      addValueChangeListener {
        update(obj) { obj.z = NumberOption(it.value) }
      }
      width = "100%"
    })

    add(TextField("Группа").apply {
      value = obj.selectionGroup ?: ""
      addValueChangeListener {
        update(obj) { obj.selectionGroup = it.value.takeIf { it.isNotBlank() } }
      }
      width = "100%"
    })

    add(TextField("Категория").apply {
      value = obj.category
      addValueChangeListener {
        update(obj) { obj.category = it.value }
      }
      width = "100%"
    })

    width = "100%"
    isPadding = false
  })

  add(TextArea("Указывать").apply {
    value = obj.designations.joinToString("\n")
    addValueChangeListener {
      update(obj) { obj.designations = it.value.lines().filter { it.isNotBlank() } }
    }
    width = "100%"
  })
}