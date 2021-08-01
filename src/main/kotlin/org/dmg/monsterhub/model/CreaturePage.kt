package org.dmg.monsterhub.model

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.HasDynamicTitle
import org.dmg.monsterhub.model.traits.TraitsService
import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.component.html.Label

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
        add(Accordion().apply {
            val superiority = creatureService.superiority(creature)
            add("Превосходство: ${superiority.value}", VerticalLayout().apply {
                add(Label("Превосходство: ${superiority.value}"))
                add(Label("Опасность: ${superiority.challengeRating}"))

                add(Label("Общее нападение: ${superiority.offence.value} (${superiority.offence.underDate})"))
                add(Label("Общая защита: ${superiority.defence.value} (${superiority.defence.underDate})"))
                add(Label("Общие черты: ${superiority.common.value} (${superiority.common.underDate})"))

                width = "100%"
                isPadding = false
                isSpacing = false
            })
            close()
        })

        add(Accordion().apply {
            val size = creatureService.size(creature)
            add("Размер $size", VerticalLayout().apply {
                add(Label("Размер $size"))
                val sizeTraits = creature
                        .getAllTraits("Размер", "Крылатый")
                        .filter { it.trait != "Размер" }
                        .map { it.toSmallString() }
                        .joinToString()
                if (sizeTraits.isNotEmpty()) {
                    add(Label(sizeTraits))
                }

                width = "100%"
                isPadding = false
                isSpacing = false
            })
            close()
        })

        add(Accordion().apply {
            add("Черты", VerticalLayout().apply {
                add(Label("Восприятие: ${creature.getAllTraits("Восприятие").map { it.toSmallString() }.joinToString()}"))
                add(Label("Движение: ${creature.getAllTraits("Движение").map { it.toSmallString() }.joinToString()}"))
                add(Label("Интеллект: ${creature.getAllTraits("Интеллект").map { it.toSmallString() }.joinToString()}"))
                add(Label("Остальные: ${creature.getAllTraits("Общее").map { it.toSmallString() }.joinToString()}"))


                width = "100%"
                isPadding = false
                isSpacing = false
            })
            close()
        })

        add(Accordion().apply {
            add("Атака", VerticalLayout().apply {
                add(Label(creature.getAllTraits("Общая атака").map { it.toSmallString() }.joinToString()))

                width = "100%"
                isPadding = false
                isSpacing = false
            })
            close()
        })

        width = "100%"
        height = "100%"
        isPadding = false
        isSpacing = false
    }

    override fun getPageTitle(): String = "MonsterHub ${creature.name}"
}