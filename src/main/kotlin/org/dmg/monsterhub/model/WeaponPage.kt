package org.dmg.monsterhub.model

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField

class WeaponPage(
    val weapon: Weapon,
    val weaponService: WeaponService
) : Dialog() {
  init {
    add(HorizontalLayout().apply {
      add(createWeaponSpace())
      add(creatAttackSpace())

      width = "100%"
      height = "100%"
      isPadding = false
    })

    width = "100%"
    height = "100%"
  }

  private fun createWeaponSpace(): VerticalLayout = VerticalLayout().apply {
    val name = TextField().apply {
      label = "Название оружия"
      value = weapon.name

      width = "100%"
    }
    name.addValueChangeListener { event ->
      weapon.name = event.value
    }
    add(name)

    val weaponFeaturesSpace = VerticalLayout().apply {
      width = "100%"
      height = "100%"
      isPadding = false
      isSpacing = false
    }

    add(HorizontalLayout().apply {
      add(Label("Свойства оружия"))
      add(Button(Icon(VaadinIcon.PLUS)) {
        val newFeature = WeaponFeature()
        weapon.features.add(newFeature)
        weaponFeaturesSpace.add(createFeatureSpace(newFeature))
      })
    })

    add(weaponFeaturesSpace)

    weapon.features.forEach { weaponFeaturesSpace.add(createFeatureSpace(it)) }

    add(HorizontalLayout().apply {
      add(Button("Сохранить") {
        weaponService.save(weapon)
      })
      add(Button("Закрыть") {
        close()
      })

      width = "100%"
      isPadding = false
    })

    width = "100%"
    height = "100%"
    isPadding = false
    isSpacing = false
  }

  private fun createFeatureSpace(feature: WeaponFeature) = HorizontalLayout().apply {
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
      weapon.features.remove(feature)
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

  private fun creatAttackSpace() = VerticalLayout().apply {
    val weaponAttacksSpace = VerticalLayout().apply {
      width = "100%"
      height = "100%"
      isPadding = false
      isSpacing = false
    }

    add(HorizontalLayout().apply {
      add(Label("Атаки оружия"))
      add(Button(Icon(VaadinIcon.PLUS)) {
        val newAttack = WeaponAttack()
        weapon.attacks.add(newAttack)
        weaponAttacksSpace.add(WeaponAttackSpace(weapon, newAttack))
      })
    })

    add(weaponAttacksSpace)

    weapon.attacks.forEach { weaponAttacksSpace.add(WeaponAttackSpace(weapon, it)) }

    width = "100%"
    height = "100%"
    isPadding = false
    isSpacing = false
  }


}