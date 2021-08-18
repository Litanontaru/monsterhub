package org.dmg.monsterhub

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.service.SettingService

@Route
class MainView(
//    creatureService: CreatureService,
//    traitsService: TraitsService,
//    weaponService: WeaponService
    settingService: SettingService
) : VerticalLayout() {
  init {
    add(Button("Базовый сеттинг") {
      settingService.save(Setting().apply {
        name = "Основание Dream"
        description = "Самый первый базовый сеттинг с основными правилами Dream."
      })
    })


    /*add(Label("Монстр"))
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
                        val creature = OldCreature()
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
    })*/
  }
}