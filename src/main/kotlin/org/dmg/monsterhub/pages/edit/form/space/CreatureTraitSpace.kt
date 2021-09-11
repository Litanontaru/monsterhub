package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object CreatureTraitSpace : AbstractCreatureSpace {
  override fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) = listOf(
      Accordion().apply {
        add("Черты", VerticalLayout().apply {
          add(Label("Восприятие: ${obj.getAllTraits("Восприятие").map { it.display() }.joinToString()}"))
          add(Label("Движение: ${obj.getAllTraits("Движение").map { it.display() }.joinToString()}"))
          add(Label("Интеллект: ${obj.getAllTraits("Интеллект").map { it.display() }.joinToString()}"))
          add(Label("Манимуляторы: ${obj.getAllTraits("Манипуляторы").map { it.display() }.joinToString()}"))
          add(Label("Остальные: ${obj.getAllTraits("Общее").map { it.display() }.joinToString()}"))

          width = "100%"
          isPadding = false
          isSpacing = false
        })

        close()
      }
  )
}