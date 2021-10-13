package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.html.ListItem
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.GameCharacter
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.service.AttackService

object GameCharacterAttackSpace : AbstractGameCharacterSpace {
  override fun use(obj: GameCharacter, locator: ServiceLocator, update: (Any, () -> Unit) -> Any) = listOf(
      Details().apply {
        summaryText = "Атака"

        addContent(VerticalLayout().apply {
          val actions = AttackService.actions(obj, locator.weaponRepository, locator.settings)
          actions
              .sortedWith(compareBy({ -it.speed }, { -it.distance }, { -it.finesseSum }, { -it.damageSum }))
              .forEach { action ->
                add(Label(action.display()))
                action.attacks.map { ListItem(it.display()) }.forEach { add(it) }
              }

          width = "100%"
          isPadding = false
          isSpacing = false
        })

        isOpened = locator.config.spaces.getOrDefault(CreatureAttackSpace, false) as Boolean
        this.addOpenedChangeListener {
          locator.config.spaces[CreatureAttackSpace] = it.isOpened
        }
      }
  )
}