package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.service.CreatureService

object CreatureSizeSpace : AbstractCreatureSpace {
  override fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) = listOf(
      Accordion().apply {
        val size = CreatureService.size(obj)
        val physicalSize = CreatureService.physicalSize(obj)
        add("Размер $size ($physicalSize)", VerticalLayout().apply {
          add(Label("Размер $size"))
          add(Label("Физический размер $physicalSize"))
          val sizeTraits = obj
              .getAllTraits("Размер", "Крылатый")
              .filter { it.feature.name != "Размер" }
              .map { it.display() }
              .joinToString()

          if (sizeTraits.isNotEmpty()) {
            add(Label(sizeTraits))
          }

          width = "100%"
          isPadding = false
          isSpacing = false
        })
        close()
      }
  )

}