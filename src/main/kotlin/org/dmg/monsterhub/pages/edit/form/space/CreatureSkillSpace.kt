package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object CreatureSkillSpace : AbstractCreatureSpace {
  override fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) = listOf(
      Accordion().apply {
        add("Способности", VerticalLayout().apply {
          val perks = obj.getAllTraits("Перк").mapNotNull { it.features.singleOrNull()?.feature?.name }
          if (perks.toList().isNotEmpty()) {
            add(Label("Перки: ${perks.joinToString()}"))
          }
          val advanced = obj.getAllTraits("Продвинутая проверка").mapNotNull { it.features.singleOrNull()?.feature?.name }
          if (advanced.toList().isNotEmpty()) {
            add(Label("Продвинутая проверка: ${advanced.joinToString()}"))
          }
          val weak = obj.getAllTraits("Слабая проверка").mapNotNull { it.features.singleOrNull()?.feature?.name }
          if (weak.toList().isNotEmpty()) {
            add(Label("Слабая проверка: ${weak.joinToString()}"))
          }
          val failed = obj.getAllTraits("Провальная проверка").mapNotNull { it.features.singleOrNull()?.feature?.name }
          if (failed.toList().isNotEmpty()) {
            add(Label("Провальная проверка: ${failed.joinToString()}"))
          }

          width = "100%"
          isPadding = false
          isSpacing = false
        })
        close()
      }
  )
}