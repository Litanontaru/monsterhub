package org.dmg.monsterhub.model

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.HasDynamicTitle
import org.dmg.monsterhub.model.traits.TraitsService

class CreaturePage(
        val creature: Creature,
        val creatureService: CreatureService,
        val traitsService: TraitsService
) : Dialog(), HasDynamicTitle {

    init {
        add(HorizontalLayout().apply {
            add(createEditSpace())
            add(createInformationSpace())

            width = "100%"
            height = "100%"
            isPadding = false
        })

        width = "100%"
        height = "100%"

    }

    private fun createEditSpace() = VerticalLayout().apply {
        val name = TextField().apply {
            label = "Имя монстра"
            isReadOnly = true

            value = creature.name

            width = "100%"
        }
        add(name)

        add(CreatureBaseSpace(creature, creatureService))
        add(CreatureTraitSpace(creature, traitsService))

        add(HorizontalLayout().apply {
            add(Button("Сохранить") {
                creatureService.save(creature)
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

    private fun createInformationSpace() = VerticalLayout().apply {
        width = "100%"
        height = "100%"
        isPadding = false
    }

    override fun getPageTitle(): String = "MonsterHub ${creature.name}"
}