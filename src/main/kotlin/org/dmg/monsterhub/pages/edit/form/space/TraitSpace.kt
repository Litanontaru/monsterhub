package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.Trait
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object TraitSpace : Space {
  override fun support(obj: Any) = obj is Trait

  override fun use(parent: HasComponents, anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
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
      val perception = VerticalLayout().apply {
        add(TextField("Восприятие").apply {
          value = obj.perceptionBase ?: ""
          addValueChangeListener {
            update(obj) { obj.perceptionBase = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        add(TextField().apply {
          value = obj.perceptionAlt ?: ""
          addValueChangeListener {
            update(obj) { obj.perceptionAlt = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        width = "100%"
        isPadding = false
        isSpacing = false
      }
      val hands = VerticalLayout().apply {
        add(TextField("Манипуляторы").apply {
          value = obj.handsBase ?: ""
          addValueChangeListener {
            update(obj) { obj.handsBase = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        add(TextField().apply {
          value = obj.handsAlt ?: ""
          addValueChangeListener {
            update(obj) { obj.handsAlt = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        width = "100%"
        isPadding = false
        isSpacing = false
      }
      val move = VerticalLayout().apply {
        add(TextField("Движение").apply {
          value = obj.moveBase ?: ""
          addValueChangeListener {
            update(obj) { obj.moveBase = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        add(TextField().apply {
          value = obj.moveAlt ?: ""
          addValueChangeListener {
            update(obj) { obj.moveAlt = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        width = "100%"
        isPadding = false
        isSpacing = false
      }
      val common = TextField("Общее").apply {
        value = obj.common ?: ""
        addValueChangeListener {
          update(obj) { obj.common = it.value.takeIf { it.isNotBlank() } }
        }
        width = "100%"
      }

      add(offence, defence, perception, hands, move, common)
      expand(offence, defence, perception, hands, move, common)

      width = "100%"
      isPadding = false
    })
  }
}