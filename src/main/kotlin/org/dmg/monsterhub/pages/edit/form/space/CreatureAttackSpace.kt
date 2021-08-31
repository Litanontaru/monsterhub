package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.service.CreatureService

object CreatureAttackSpace : AbstractCreatureSpace {
  override fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) = listOf(
      Accordion().apply {
        add("Атака", VerticalLayout().apply {
          add(Label(obj.getAllTraits("Общая атака").map { it.display() }.joinToString()))

          CreatureService.naturalWeapons(obj)
              .let { locator.weaponRepository.findAllByNameInAndSettingIn(it, locator.settigs) }
              .flatMap { weapon -> weapon.attacks.map { weapon to it } }
              .forEach { (weapon, attack) -> add(Label(attack.display(weapon.name, weapon.features))) }

          width = "100%"
          isPadding = false
          isSpacing = false
        })

        close()
      }
  )
}