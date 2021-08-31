package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object CreatureDefence : AbstractCreatureSpace {
  override fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) = listOf(
      Accordion().apply {
        add("Защита", VerticalLayout().apply {
          add(Label(obj.getAllTraits("Тип тела").map { it.display() }.joinToString()))
          add(Label(obj.getAllTraits("Регенерация").map { it.display() }.joinToString()))
          obj.getAllTraits("Особая защита").forEach { add(Label(it.display())) }
          add(Label(obj.getAllTraits("Общая защита").map { it.display() }.joinToString()))

          width = "100%"
          isPadding = false
          isSpacing = false
        })

        close()
      }
  )
}