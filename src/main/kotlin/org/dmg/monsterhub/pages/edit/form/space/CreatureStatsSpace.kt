package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.service.CreatureService

class CreatureStatsSpace(
    private val creature: Creature
) : VerticalLayout() {
  init {
    add(Accordion().apply {
      val superiority = CreatureService.superiority(creature)
      add("Превосходство: ${superiority.value}", VerticalLayout().apply {
        add(Label("Превосходство: ${superiority.value}"))
        add(Label("Опасность: ${superiority.challengeRating}"))

        add(Label("Общее нападение: ${superiority.offence.value} (${superiority.offence.underDate})"))
        add(Label("Общая защита: ${superiority.defence.value} (${superiority.defence.underDate})"))
        add(Label("Общие черты: ${superiority.common.value} (${superiority.common.underDate})"))

        width = "100%"
        isPadding = false
        isSpacing = false
      })
    })

    width = "100%"
    height = "100%"
    isPadding = false
    isSpacing = false
  }
}