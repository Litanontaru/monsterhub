package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.form.TraitRatePage
import org.dmg.monsterhub.service.CreatureService

object SuperioritySpace : AbstractCreatureSpace {
  override fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Any) = listOf(
      Details().apply {
        val superiority = CreatureService.superiority(obj)
        summaryText = "Превосходство: ${superiority.value}"

        addContent(VerticalLayout().apply {
          add(Label("Превосходство: ${superiority.value}"))
          add(Label("Недобор: ${superiority.underRate}"))
          add(Label("База: ${superiority.base}"))

          add(Label("Нападение бонус: ${superiority.offMax.second} : ${superiority.offMax.first.joinToString()}"))
          add(Label("Нападение штраф: ${superiority.offMin.second} : ${superiority.offMin.first.joinToString()}"))

          add(Label("Защита бонус: ${superiority.defMax.second} : ${superiority.defMax.first.joinToString()}"))
          add(Label("Защита штраф: ${superiority.defMin.second} : ${superiority.defMin.first.joinToString()}"))

          add(Label("Общее бонус: ${superiority.comMax.second} : ${superiority.comMax.first.joinToString()}"))
          add(Label("Общее штраф: ${superiority.comMin.second} : ${superiority.comMin.first.joinToString()}"))

          add(Button("Детали") {
            TraitRatePage(obj).open()
          })

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