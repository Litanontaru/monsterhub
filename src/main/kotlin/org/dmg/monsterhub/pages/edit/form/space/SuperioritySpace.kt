package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.service.CreatureService

object SuperioritySpace : AbstractCreatureSpace {
  override fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Any) = listOf(
      Details().apply {
        val superiority = CreatureService.superiority(obj)
        summaryText = "Превосходство: ${superiority.value}"

        addContent(VerticalLayout().apply {
          add(Label("Превосходство: ${superiority.value}"))
          add(Label("Перекос: ${superiority.skew}"))
          add(Label("Опасность: ${superiority.challengeRating}"))

          add(Label("Общее нападение: ${superiority.offence.value} (${superiority.offence.underDate})"))
          add(Label("Общая защита: ${superiority.defence.value} (${superiority.defence.underDate})"))
          add(Label("Общие черты: ${superiority.common.value} (${superiority.common.underDate})"))

          width = "100%"
          isPadding = false
          isSpacing = false
        })

        isOpened = locator.config.spaces.getOrDefault(SuperioritySpace, false) as Boolean
        this.addOpenedChangeListener {
          locator.config.spaces[SuperioritySpace] = it.isOpened
        }
      }
  )
}