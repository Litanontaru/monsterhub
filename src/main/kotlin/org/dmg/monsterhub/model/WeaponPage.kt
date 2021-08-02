package org.dmg.monsterhub.model

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
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

        add(Label("Свойства оружия"))
        weapon.features.forEach { feature ->
            add(HorizontalLayout().apply {
                val featureName = TextField().apply {
                    value = feature.feature

                    width = "100%"
                }
                featureName.addValueChangeListener { event ->
                    feature.feature = event.value
                }

                val x = TextField().apply {
                    value = feature.primaryNumber.toString()
                    addValueChangeListener {
                        try {
                            feature.primaryNumber = Integer.parseInt(it.value)
                        } catch (e: NumberFormatException) {
                            Notification("Оружие хочет тут число").apply { duration = 1000 }.open()
                        }
                    }

                    width = "4em"
                }
                val y = TextField().apply {
                    value = feature.secondaryNumber.toString()
                    addValueChangeListener {
                        try {
                            feature.secondaryNumber = Integer.parseInt(it.value)
                        } catch (e: NumberFormatException) {
                            Notification("Оружие хочет тут число").apply { duration = 1000 }.open()
                        }
                    }

                    width = "4em"
                }

                val detailsButton = Button(Icon(VaadinIcon.FILE_TEXT)) {
                    DetailsDialog(feature).open()
                }

                val deleteButton = Button(Icon(VaadinIcon.TRASH)) {
                    weapon.features.remove(feature)
                    this.isVisible = false
                }

                add(featureName)
                add(x)
                add(y)
                add(detailsButton)
                add(deleteButton)

                width = "100%"
                isPadding = false
            })
        }

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
}