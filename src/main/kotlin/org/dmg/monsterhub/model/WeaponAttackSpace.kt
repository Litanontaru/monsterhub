package org.dmg.monsterhub.model

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField

class WeaponAttackSpace(
    val weapon: Weapon,
    val weaponAttack: WeaponAttack
) : VerticalLayout() {
  init {
    val space = this;

    add(HorizontalLayout().apply {
      val mode = TextField().apply {
        value = weaponAttack.mode

        width = "100%"
      }
      mode.addValueChangeListener { event -> weaponAttack.mode = event.value }

      val damage = IntField(
          { weaponAttack.damage },
          { weaponAttack.damage = it },
          "Атака хочет тут число"
      )
      val desturction = IntField(
          { weaponAttack.desturction },
          { weaponAttack.desturction = it },
          "Атака хочет тут число"
      )
      val distance = DoubleField(
          { weaponAttack.distance },
          { weaponAttack.distance = it },
          "Атака хочет тут число"
      )
      val speed = IntField(
          { weaponAttack.speed },
          { weaponAttack.speed = it },
          "Атака хочет тут число"
      )
      val deleteButton = Button(Icon(VaadinIcon.TRASH)) {
        weapon.attacks.remove(weaponAttack)
        space.isVisible = false
      }

      add(mode)
      add(damage)
      add(desturction)
      add(distance)
      add(speed)
      add(deleteButton)

      width = "100%"
      isPadding = false
    })

    val attackFeaturesSpace = VerticalLayout().apply {
      width = "100%"
      height = "100%"
      isPadding = false
      isSpacing = false
    }

    add(HorizontalLayout().apply {
      add(Label("Свойства атаки"))
      add(Button(Icon(VaadinIcon.PLUS)) {
        val newFeature = WeaponAttackFeature()
        weaponAttack.features.add(newFeature)
        attackFeaturesSpace.add(createFeatureSpace(newFeature))
      })
    })

    add(attackFeaturesSpace)

    weaponAttack.features.forEach { attackFeaturesSpace.add(createFeatureSpace(it)) }

    width = "100%"
    isPadding = false
    isSpacing = false
  }

  private fun createFeatureSpace(feature: WeaponAttackFeature) = HorizontalLayout().apply {
    val featureName = TextField().apply {
      value = feature.feature

      width = "100%"
    }
    featureName.addValueChangeListener { event ->
      feature.feature = event.value
    }

    val primaryNumber = IntField(
        { feature.primaryNumber },
        { feature.primaryNumber = it },
        "Оружие хочет тут число"
    )
    val secondaryNumber = IntField(
        { feature.secondaryNumber },
        { feature.secondaryNumber = it },
        "Оружие хочет тут число"
    )

    val detailsButton = Button(Icon(VaadinIcon.FILE_TEXT)) {
      DetailsDialog(feature).open()
    }

    val deleteButton = Button(Icon(VaadinIcon.TRASH)) {
      weaponAttack.features.remove(feature)
      this.isVisible = false
    }

    add(featureName)
    add(primaryNumber)
    add(secondaryNumber)
    add(detailsButton)
    add(deleteButton)

    width = "100%"
    isPadding = false
  }

}