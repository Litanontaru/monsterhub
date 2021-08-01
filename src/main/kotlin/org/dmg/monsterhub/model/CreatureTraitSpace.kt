package org.dmg.monsterhub.model

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.model.traits.Trait
import org.dmg.monsterhub.model.traits.TraitsService

class CreatureTraitSpace(
        val creature: Creature,
        val traitsService: TraitsService
): VerticalLayout() {
    class TraitSpace(val creatureTrait: CreatureTrait) : HorizontalLayout()

    val traitSpaces = mutableListOf<TraitSpace>()

    init {
        add(Label("Черты"))
        val traitsLayout = VerticalLayout().apply {
            creature.traits.map { createTraitSpace(it) }.forEach {
                traitSpaces.add(it)
                add(it)
            }

            width = "100%"
            isPadding = false
            isSpacing = false
        }
        add(traitsLayout)
        add(createAddTrait {
            val space = createTraitSpace(it)
            traitSpaces.add(space)
            traitsLayout.add(space)
        })

        width = "100%"
        isPadding = false
        isSpacing = false
    }

    private fun createTraitSpace(trait: CreatureTrait) = TraitSpace(trait).apply {
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
            removeTrait(trait, result)
        }

        add(name)
        add(x)
        add(y)
        add(detailsButton)
        add(deleteButton)

        width = "100%"
        isPadding = false
    }

    private fun removeTrait(trait: CreatureTrait, result: TraitSpace) {
        creature.traits.remove(trait)
        result.isVisible = false
    }

    private fun createAddTrait(onAdd: (CreatureTrait) -> Unit) = HorizontalLayout().apply {
        val name = TextField().apply {
            width = "100%"

            value = ""
        }
        var theTrait: Trait? = null

        fun tryAddTrait(setTrait: Trait, onAdd: (CreatureTrait) -> Unit, name: TextField) {
            val sameName = creature.traits.find { it.trait == setTrait.name }
            if (sameName != null) {
                AddSameTraitDialog(sameName.trait, null) {
                    traitSpaces
                            .find { it.creatureTrait.trait == sameName.trait }
                            ?.apply { removeTrait(creatureTrait, this) }
                    tryAddTrait(setTrait, onAdd, name)
                }.open()
                return
            }

            val sameGroup = when {
                setTrait.group == null -> null
                else -> creature.traits.find { setTrait.group == it.traitGroup }
            }
            if (sameGroup != null) {
                AddSameTraitDialog(sameGroup.trait, sameGroup.traitGroup) {
                    traitSpaces
                            .find { it.creatureTrait.trait == sameGroup.trait }
                            ?.apply { removeTrait(creatureTrait, this) }
                    tryAddTrait(setTrait, onAdd, name)
                }.open()
                return
            }

            val newCreatureTrait = CreatureTrait().apply {
                trait = setTrait.name
                traitGroup = setTrait.group
            }

            creature.traits.add(newCreatureTrait)
            onAdd(newCreatureTrait)

            name.value = ""

            theTrait = null
        }

        val add = Button(Icon(VaadinIcon.PLUS))
        add.addClickListener {
            theTrait?.let { tryAddTrait(it, onAdd, name) }
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
}