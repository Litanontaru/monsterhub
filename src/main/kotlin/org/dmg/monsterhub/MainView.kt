package org.dmg.monsterhub

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import org.dmg.monsterhub.model.Creature
import org.dmg.monsterhub.model.CreaturePage
import org.dmg.monsterhub.model.CreatureService

@Route
class MainView(
    creatureService: CreatureService
) : VerticalLayout() {
    init {
        val name = TextField().apply {
            width = "100%"
        }

        add(name)
        add(HorizontalLayout().apply {
            add(Button("Найти", Icon(VaadinIcon.EDIT)) {
                creatureService.find(name.value)
                        ?. let { CreaturePage(it, creatureService).open() }
                        ?: Notification("Монстр в логове не найден").open()
            })
            add(Button("Создать", Icon(VaadinIcon.PLUS)) {
                creatureService.find(name.value)
                        ?. let { Notification("${name.value} уже живёт в логове")}
                        ?: run {
                            val creature = Creature()
                            creature.name = name.value
                            creatureService.save(creature)

                            CreaturePage(creature, creatureService).open()
                        }
            })

            width = "100%"
        })
    }
}