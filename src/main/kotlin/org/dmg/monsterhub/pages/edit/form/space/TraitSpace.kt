package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.Trait
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object TraitSpace : Space {
  override fun support(obj: Any) = obj is Trait

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val parent = mutableListOf<Component>()
    val obj = anyObj as Trait

    parent.add(Label("Показатели черты"))
    parent.add(TextField().apply {
      value = obj.base ?: ""
      addValueChangeListener {
        update(obj) { obj.base = it.value.takeIf { it.isNotBlank() } }
      }
    })

    parent.add(HorizontalLayout().apply {
      val offence = TextField("Напападение").apply {
        value = obj.offenceAlt ?: ""
        addValueChangeListener {
          update(obj) { obj.offenceAlt = it.value.takeIf { it.isNotBlank() } }
        }
        width = "100%"
      }
      val defence = TextField("Защита").apply {
        value = obj.defenceAlt ?: ""
        addValueChangeListener {
          update(obj) { obj.defenceAlt = it.value.takeIf { it.isNotBlank() } }
        }
        width = "100%"
      }

      val common = TextField("Общее").apply {
        value = obj.commonAlt ?: ""
        addValueChangeListener {
          update(obj) { obj.commonAlt = it.value.takeIf { it.isNotBlank() } }
        }
        width = "100%"
      }

      add(offence, defence, common)
      expand(offence, defence, common)

      width = "100%"
      isPadding = false
    })

    parent.add(Checkbox("Перекрывающее").apply {
      value = obj.overriding
      addValueChangeListener {
        update(obj) { obj.overriding = it.value }
      }
    })

    return parent
  }
}