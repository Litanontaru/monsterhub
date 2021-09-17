package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.html.ListItem
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.service.AttackService
import org.dmg.monsterhub.service.CreatureService

object CreatureAttackSpace : AbstractCreatureSpace {
  override fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Any) = listOf(
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

          add(Label(obj.getAllTraits("Общая атака").map { it.display() }.joinToString()))

          val attackFeatures = obj.getAllTraits("Свойство атаки")
              .groupBy { it.designations.find { it.designationKey == "Естественное оружие" }!!.value }

          val sizeProfile = CreatureService.sizeProfile(obj)

          CreatureService.naturalWeapons(obj)
              .let { locator.weaponRepository.findAllByNameInAndSettingIn(it.map { it.first }, locator.settings) }
              .asSequence()
              .map { it.adjust(sizeProfile, true, attackFeatures.getOrDefault(it.name, emptyList())) }
              .flatMap { weapon -> weapon.attacks.asSequence().map { weapon to it } }
              .map { (weapon, attack) -> attack.display(weapon.name, weapon.features) }
              .forEach { add(Label(it)) }

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