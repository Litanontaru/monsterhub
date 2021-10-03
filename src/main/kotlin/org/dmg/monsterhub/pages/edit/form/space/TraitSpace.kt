package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.Trait
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object TraitSpace : Space {
  override fun support(obj: Any) = obj is Trait

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val parent = mutableListOf<Component>()
    val obj = anyObj as Trait

    parent.add(Label("Показатели черты"))

    parent.add(HorizontalLayout().apply {
      val offence = VerticalLayout().apply {
        add(TextField("Напападение").apply {
          value = obj.offenceBase ?: ""
          addValueChangeListener {
            update(obj) { obj.offenceBase = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        add(TextField().apply {
          value = obj.offenceAlt ?: ""
          addValueChangeListener {
            update(obj) { obj.offenceAlt = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })
        width = "100%"
        isPadding = false
        isSpacing = false
      }
      val defence = VerticalLayout().apply {
        add(TextField("Защита").apply {
          value = obj.defenceBase ?: ""
          addValueChangeListener {
            update(obj) { obj.defenceBase = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        add(TextField().apply {
          value = obj.defenceAlt ?: ""
          addValueChangeListener {
            update(obj) { obj.defenceAlt = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        width = "100%"
        isPadding = false
        isSpacing = false
      }

      val common = VerticalLayout().apply {
        add(TextField("Общее").apply {
          value = obj.common ?: ""
          addValueChangeListener {
            update(obj) { obj.common = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        add(TextField().apply {
          value = obj.commonAlt ?: ""
          addValueChangeListener {
            update(obj) { obj.commonAlt = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        width = "100%"
        isPadding = false
        isSpacing = false
      }

      add(offence, defence, common)
      expand(offence, defence, common)

      width = "100%"
      isPadding = false
    })

    return parent
  }
}