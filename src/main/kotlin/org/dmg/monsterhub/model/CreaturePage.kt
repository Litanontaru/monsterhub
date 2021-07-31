package org.dmg.monsterhub.model

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.HasDynamicTitle

class CreaturePage(
        val creature: Creature,
        val creatureService: CreatureService
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

        var theBase = base
        val name = TextField().apply {
            value = base.name

            width = "100%"
        }
        name.addValueChangeListener {
            creatureService.find(it.value)
                    ?.let { theBase = it }
                    ?: run { name.value = theBase.name }
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

        val add = Button(Icon(VaadinIcon.PLUS)).apply {
            isEnabled = false
        }
        add.addClickListener {
            theBase?.let {
                creature.base.add(it)
                onAdd(it)
                theBase = null
                add.isEnabled = false
            }
        }

        name.addValueChangeListener {
            creatureService.find(it.value)
                    ?.let {
                        theBase = it
                        add.isEnabled = true
                    }
                    ?: run {
                        theBase = null
                        add.isEnabled = false
                    }
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