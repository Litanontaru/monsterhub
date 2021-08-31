package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.service.CreatureService

object SuperioritySpace : Space {
  override fun support(obj: Any) = obj is Creature

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit): List<Component> {
    val creature = anyObj as Creature

    return listOf(Accordion().apply {
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
  }
}