package org.dmg.monsterhub.model

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField

class CreatureBaseSpace(
    val creature: OldCreature,
    val creatureService: CreatureService
) : VerticalLayout() {
  init {
    add(Label("Основан на монстрах"))
    val baseLayout = VerticalLayout().apply {
      creature.base.forEach { add(createBaseSpace(it)) }

      width = "100%"
      isPadding = false
      isSpacing = false
    }
    add(baseLayout)
    add(createAddBaseCreature {
      baseLayout.add(createBaseSpace(it))
    })

    width = "100%"
    isPadding = false
    isSpacing = false
  }

  private fun createBaseSpace(base: OldCreature) = HorizontalLayout().apply {
    val result = this

    val name = TextField().apply {
      value = base.name

      width = "100%"
      isReadOnly = true
    }

    val delete = Button(Icon(VaadinIcon.TRASH)) {
      creature.base.remove(base)
      result.isVisible = false
    }

    add(name)
    add(delete)

    width = "100%"
    isPadding = false
  }

  private fun createAddBaseCreature(onAdd: (OldCreature) -> Unit) = HorizontalLayout().apply {
    val name = TextField().apply {
      width = "100%"

      value = ""
    }
    var theBase: OldCreature? = null

    val add = Button(Icon(VaadinIcon.PLUS))
    add.addClickListener {
      theBase?.let {
        creature.base.add(it)
        onAdd(it)
        theBase = null
        name.value = ""
      }
    }

    name.addValueChangeListener {
      creatureService.find(it.value)
          ?.let { theBase = it }
          ?: run { theBase = null }
    }

    add(name)
    add(add)

    width = "100%"
    isPadding = false
  }
}