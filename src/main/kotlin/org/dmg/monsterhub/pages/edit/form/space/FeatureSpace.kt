package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.ContainerData
import org.dmg.monsterhub.data.Faction
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.meta.NumberOption
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object FeatureSpace : Space {
  override fun support(obj: Any) = obj is Feature && obj !is ContainerData && obj !is Faction

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val parent = mutableListOf<Component>()
    val obj = anyObj as Feature

    parent.add(HorizontalLayout().apply {
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

    parent.add(TextArea("Указывать").apply {
      value = obj.designations.joinToString("\n")
      addValueChangeListener {
        update(obj) { obj.designations = it.value.lines().filter { it.isNotBlank() } }
      }
      width = "100%"
    })

    return parent
  }
}
