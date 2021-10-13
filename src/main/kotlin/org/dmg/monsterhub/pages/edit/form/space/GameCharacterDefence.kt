package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.GameCharacter
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.service.AttackEffectiveness
import org.dmg.monsterhub.service.DamageService
import org.dmg.monsterhub.service.WeaponType

object GameCharacterDefence : AbstractGameCharacterSpace {
  override fun use(obj: GameCharacter, locator: ServiceLocator, update: (Any, () -> Unit) -> Any) = listOf(
      Details().apply {
        summaryText = "Защита"

        addContent(VerticalLayout().apply {
          val damage = TextField("Урон").apply {
            this.value = "0"
          }
          val destruction = TextField("Разрушение").apply {
            this.value = "0"
          }

          add(HorizontalLayout().apply {
            val slash = Label("/")
            add(damage, slash, destruction)
            setVerticalComponentAlignment(FlexComponent.Alignment.END, damage, slash, destruction)
          })

          val effectiveness = RadioButtonGroup<AttackEffectiveness>().apply {
            label = "Попадание"
            setItems(AttackEffectiveness.values().toList())
            value = AttackEffectiveness.STANDARD
          }

          val weaponType = RadioButtonGroup<WeaponType>().apply {
            label = "Тип оружия"
            setItems(WeaponType.values().toList())
            value = WeaponType.OTHER
          }

          val defenceProfile = DamageService.defenceProfile(obj)

          val resultDamage = Label()

          val button = Button("Нанести урон") {
            val result = defenceProfile.damage(
                damage.value.toIntOrNull() ?: 0,
                destruction.value.toIntOrNull() ?: 0,
                effectiveness.value,
                weaponType.value
            )
            resultDamage.text = result.display()
          }

          add(effectiveness, weaponType, button, resultDamage)

          isPadding = false
          isSpacing = false
        })

        isOpened = locator.config.spaces.getOrDefault(GameCharacterDefence, false) as Boolean
        this.addOpenedChangeListener {
          locator.config.spaces[GameCharacterDefence] = it.isOpened
        }
      }
  )
}