package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.service.CreatureService
import org.dmg.monsterhub.service.SizeProfileService

object CreatureAttackSpace : AbstractCreatureSpace {
  override fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) = listOf(
      Accordion().apply {
        add("Атака", VerticalLayout().apply {
          add(Label(obj.getAllTraits("Общая атака").map { it.display() }.joinToString()))

          val attackFeatures = obj.getAllTraits("Свойство атаки")
              .groupBy { it.designations.find { it.designationKey == "Естественное оружие" }!!.value }

          val sizeProfile = CreatureService.sizeProfile(obj)

          CreatureService.naturalWeapons(obj)
              .let { locator.weaponRepository.findAllByNameInAndSettingIn(it, locator.settigs) }
              .asSequence()
              .map { it.adjust(sizeProfile, true, attackFeatures.getOrDefault(it.name, emptyList())) }
              .flatMap { weapon -> weapon.attacks.asSequence().map { weapon to it } }
              .map { (weapon, attack) -> attack.display(weapon.name, weapon.features) }
              .forEach { add(Label(it)) }

          width = "100%"
          isPadding = false
          isSpacing = false
        })

        close()
      }
  )
}