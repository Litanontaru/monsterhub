package org.dmg.monsterhub.model

import com.vaadin.flow.component.accordion.Accordion
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.HasDynamicTitle

class CreaturePage(
    private val creature: OldCreature,
    private val creatureService: CreatureService,
    private val traitsService: TraitsService
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

        close()
        CreaturePage(creature, creatureService, traitsService).open()
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
      val physicalSize = creatureService.physicalSize(creature)
      add("Размер $size ($physicalSize)", VerticalLayout().apply {
        add(Label("Размер $size"))
        add(Label("Физический размер $physicalSize"))
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
        add(Label("Восприятие: ${creature.getAllTraits("Восприятие").map { it.toBigString() }.joinToString()}"))
        add(Label("Движение: ${creature.getAllTraits("Движение").map { it.toBigString() }.joinToString()}"))
        add(Label("Интеллект: ${creature.getAllTraits("Интеллект").map { it.toBigString() }.joinToString()}"))
        add(Label("Остальные: ${creature.getAllTraits("Общее").map { it.toBigString() }.joinToString()}"))

        width = "100%"
        isPadding = false
        isSpacing = false
      })
      close()
    })

    add(Accordion().apply {
      add("Атака", VerticalLayout().apply {
        add(Label(creature.getAllTraits("Общая атака").map { it.toBigString() }.joinToString()))

        creatureService
            .weapons(creature)
            .flatMap { weapon -> weapon.attacks.map { weapon to it } }
            .forEach { (weapon, attack) ->
              val features = (weapon.features + attack.features)
                  .map {
                    it.feature +
                        (if (it.primaryNumber != 0) " ${it.primaryNumber}" else "") +
                        (if (it.secondaryNumber != 0) " ${it.secondaryNumber}" else "") +
                        (if (it.details.isNotBlank()) " (${it.details})" else "")
                  }
                  .joinToString()
              add(Label("${weapon.name} ${attack.mode}, урон ${attack.damage}/${attack.desturction}, ${attack.distance} м, скр ${attack.speed}, $features"))
            }

        width = "100%"
        isPadding = false
        isSpacing = false
      })
      close()
    })

    add(Accordion().apply {
      add("Защита", VerticalLayout().apply {
        add(Label(creature.getAllTraits("Тип тела").map { it.toBigString() }.joinToString()))
        add(Label(creature.getAllTraits("Регенерация").map { it.toBigString() }.joinToString()))
        creature.getAllTraits("Особая защита").forEach { add(Label(it.toBigString())) }
        add(Label(creature.getAllTraits("Общая защита").map { it.toBigString() }.joinToString()))

        width = "100%"
        isPadding = false
        isSpacing = false
      })

      close()
    })

    add(Accordion().apply {
      add("Способности", VerticalLayout().apply {
        val perks = creature.getAllTraits("Перк Н", "Перк З", "Перк О").map { it.details }
        if (perks.toList().isNotEmpty()) {
          add(Label("Перки: ${perks.joinToString()}"))
        }
        val advanced = creature.getAllTraits("Продвинутая проверка Н", "Продвинутая проверка З", "Продвинутая проверка О").map { it.details }
        if (advanced.toList().isNotEmpty()) {
          add(Label("Продвинутая проверка: ${advanced.joinToString()}"))
        }
        val weak = creature.getAllTraits("Слабая проверка Н", "Слабая проверка З", "Слабая проверка О").map { it.details }
        if (weak.toList().isNotEmpty()) {
          add(Label("Слабая проверка: ${weak.joinToString()}"))
        }
        val failed = creature.getAllTraits("Провальная проверка Н", "Провальная проверка З", "Провальная проверка О").map { it.details }
        if (failed.toList().isNotEmpty()) {
          add(Label("Провальная проверка: ${failed.joinToString()}"))
        }

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