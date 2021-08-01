package org.dmg.monsterhub

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import org.dmg.monsterhub.model.*

@Route
class MainView(
    creatureService: CreatureService,
    traitsService: TraitsService,
    weaponService: WeaponService
) : VerticalLayout() {
    init {
        add(Label("Монстр"))
        val name = TextField().apply {
            width = "100%"
        }

        add(name)
        add(HorizontalLayout().apply {
            add(Button("Найти монстра", Icon(VaadinIcon.EDIT)) {
                creatureService.find(name.value)
                        ?. let { CreaturePage(it, creatureService, traitsService).open() }
                        ?: Notification("Монстр в логове не найден").apply { duration = 1000 }.open()
            })
            add(Button("Создать монстра", Icon(VaadinIcon.PLUS)) {
                creatureService.find(name.value)
                        ?. let { Notification("${name.value} уже живёт в логове").apply { duration = 1000 }.open()}
                        ?: run {
                            val creature = Creature()
                            creature.name = name.value
                            creatureService.save(creature)

                            CreaturePage(creature, creatureService, traitsService).open()
                        }
            })

            width = "100%"
        })

        add(Label("Оружие"))
        val wName = TextField().apply {
            width = "100%"
        }

        add(wName)
        add(HorizontalLayout().apply {
            add(Button("Найти оружие", Icon(VaadinIcon.EDIT)) {
                weaponService.find(wName.value)
                        ?. let { WeaponPage(it, weaponService).open() }
                        ?: Notification("Оружие в арсенале не найдено").apply { duration = 1000 }.open()
            })
            add(Button("Создать оружие", Icon(VaadinIcon.PLUS)) {
                weaponService.find(wName.value)
                        ?. let { Notification("${wName.value} уже описано в арсенале").apply { duration = 1000 }.open()}
                        ?: run {
                            val weapon = Weapon().apply {
                                this.name = wName.value
                            }
                            weaponService.save(weapon)

                            WeaponPage(weapon, weaponService).open()
                        }
            })

            width = "100%"
        })
    }
}