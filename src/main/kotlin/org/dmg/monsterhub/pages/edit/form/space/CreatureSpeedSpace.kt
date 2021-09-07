package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.ListItem
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.service.CreatureSpeedService

object CreatureSpeedSpace : AbstractCreatureSpace {
  override fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) = listOf(
      Details().apply {
        summaryText = "Движение"
        CreatureSpeedService
            .speed(obj)
            .map { ListItem(it.display()) }
            .forEach { addContent(it) }
      }
  )
}