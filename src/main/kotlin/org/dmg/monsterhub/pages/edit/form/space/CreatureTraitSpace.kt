package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object CreatureTraitSpace : AbstractCreatureSpace {
  override fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Any) = listOf(
      Details().apply {
        summaryText = "Черты"

        addContent(VerticalLayout().apply {
          add(Label("Восприятие: ${obj.getAllTraits("Восприятие").map { it.display() }.joinToString()}"))
          add(Label("Движение: ${obj.getAllTraits("Движение").map { it.display() }.joinToString()}"))
          add(Label("Интеллект: ${obj.getAllTraits("Интеллект").map { it.display() }.joinToString()}"))
          add(Label("Манипуляторы: ${obj.getAllTraits("Манипуляторы").map { it.display() }.joinToString()}"))
          add(Label("Остальные: ${obj.getAllTraits("Общее").map { it.display() }.joinToString()}"))

          width = "100%"
          isPadding = false
          isSpacing = false
        })

        isOpened = locator.config.spaces.getOrDefault(CreatureTraitSpace, false) as Boolean
        this.addOpenedChangeListener {
          locator.config.spaces[CreatureTraitSpace] = it.isOpened
        }
      }
  )
}