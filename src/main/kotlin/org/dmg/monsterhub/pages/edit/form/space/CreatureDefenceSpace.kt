package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.ListItem
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object CreatureDefenceSpace : AbstractCreatureSpace {
  override fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Any) = listOf(
      Details().apply {
        summaryText = "Защита"

        addContent(VerticalLayout().apply {
          obj.getAllTraits("Тип тела").map { it.display() }.joinToString().takeIf { it.isNotBlank() }?.run { add(ListItem(this)) }
          obj.getAllTraits("Регенерация").map { it.display() }.joinToString().takeIf { it.isNotBlank() }?.run { add(ListItem(this)) }
          obj.getAllTraits("Особая защита").forEach { add(ListItem(it.display())) }
          obj.getAllTraits("Общая защита").map { it.display() }.joinToString().takeIf { it.isNotBlank() }?.run { add(ListItem(this)) }

          isPadding = false
          isSpacing = false
        })

        isOpened = locator.config.spaces.getOrDefault(CreatureDefenceSpace, false) as Boolean
        this.addOpenedChangeListener {
          locator.config.spaces[CreatureDefenceSpace] = it.isOpened
        }
      }
  )
}