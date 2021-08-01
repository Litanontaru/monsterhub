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
import com.vaadin.flow.router.HasDynamicTitle
import org.dmg.monsterhub.model.traits.Trait
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

        createBaseCreatures()
        createTraits()

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

    private fun VerticalLayout.createBaseCreatures() {
        add(Label("Основан на монстрах"))
        val baseLayout = VerticalLayout().apply {
            creature.base.forEach { add(createBaseSpace(it)) }

            width = "100%"
            isPadding = false
            isSpacing = false
        }
        add(baseLayout)
        add(createAddBaseCreature {
            baseLayout.add(createBaseSpace(it))
        })
    }

    private fun createBaseSpace(base: Creature) = HorizontalLayout().apply {
        val result = this

        val name = TextField().apply {
            value = base.name

            width = "100%"
            isReadOnly = true
        }

        val delete = Button(Icon(VaadinIcon.TRASH)) {
            creature.base.remove(base)
            result.isVisible = false
        }

        add(name)
        add(delete)

        width = "100%"
        isPadding = false
    }

    private fun createAddBaseCreature(onAdd: (Creature) -> Unit) = HorizontalLayout().apply {
        val name = TextField().apply {
            width = "100%"

            value = ""
        }
        var theBase: Creature? = null

        val add = Button(Icon(VaadinIcon.PLUS))
        add.addClickListener {
            theBase?.let {
                creature.base.add(it)
                onAdd(it)
                theBase = null
                name.value = ""
            }
        }

        name.addValueChangeListener {
            creatureService.find(it.value)
                    ?.let { theBase = it }
                    ?: run { theBase = null }
        }

        add(name)
        add(add)

        width = "100%"
        isPadding = false
    }

    private fun VerticalLayout.createTraits() {
        add(Label("Черты"))
        val traitsLayout = VerticalLayout().apply {
            creature.traits.forEach { add(createTraitSpace(it)) }

            width = "100%"
            isPadding = false
            isSpacing = false
        }
        add(traitsLayout)
        add(createAddTrait {
            traitsLayout.add(createTraitSpace(it))
        })
    }

    private fun createTraitSpace(trait: CreatureTrait) = HorizontalLayout().apply {
        val result = this
        val name = TextField().apply {
            value = trait.trait

            width = "100%"
        }
        name.addValueChangeListener {
            traitsService.get(it.value)
                    ?.let {
                        trait.trait = it.name
                        trait.traitGroup = it.group
                    }
                    ?: run { name.value = trait.trait }
        }

        val x = TextField().apply {
            value = trait.x.toString()
            addValueChangeListener {
                try {
                    trait.x = Integer.parseInt(it.value)
                } catch (e: NumberFormatException) {
                    Notification("Монст хочет тут число").apply { duration = 1000 }.open()
                }
            }

            width = "4em"
        }
        val y = TextField().apply {
            value = trait.y.toString()
            addValueChangeListener {
                try {
                    trait.y = Integer.parseInt(it.value)
                } catch (e: NumberFormatException) {
                    Notification("Монст хочет тут число").apply { duration = 1000 }.open()
                }
            }

            width = "4em"
        }

        val detailsButton = Button(Icon(VaadinIcon.FILE_TEXT)) {
            CreatureTraitDetails(trait).open()
        }

        val deleteButton = Button(Icon(VaadinIcon.TRASH)) {
            creature.traits.remove(trait)
            result.isVisible = false
        }

        add(name)
        add(x)
        add(y)
        add(detailsButton)
        add(deleteButton)

        width = "100%"
        isPadding = false
    }

    private fun createAddTrait(onAdd: (CreatureTrait) -> Unit) = HorizontalLayout().apply {
        val name = TextField().apply {
            width = "100%"

            value = ""
        }
        var theTrait: Trait? = null

        val add = Button(Icon(VaadinIcon.PLUS))
        add.addClickListener {
            theTrait?.let {
                val newCreatureTrait = CreatureTrait().apply {
                    trait = it.name
                    traitGroup = it.group
                }
                creature.traits.add(newCreatureTrait)
                onAdd(newCreatureTrait)

                theTrait = null
                name.value = ""
            }
        }

        name.addValueChangeListener {
            traitsService.get(it.value)
                    ?.let { theTrait = it }
                    ?: run { theTrait = null }
        }

        add(name)
        add(add)

        width = "100%"
        isPadding = false
    }

    private fun createInformationSpace() = VerticalLayout().apply {
        width = "100%"
        height = "100%"
        isPadding = false
    }

    override fun getPageTitle(): String = "MonsterHub ${creature.name}"
}