package org.dmg.monsterhub.pages.edit.form

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.Trait
import org.dmg.monsterhub.service.Formula.toFormula

class TraitRatePage(
    creature: Creature
) : Dialog() {
  init {
    add(VerticalLayout().apply {
      val grid = Grid<FeatureData>().apply {
        addColumn { it.display() }.apply {
          setHeader("Черта")
          isAutoWidth = true
          flexGrow = 2
          isSortable = true
        }
        addColumn { it.feature.category }.apply {
          setHeader("Категория")
          isAutoWidth = true
          isSortable = true
        }
        addColumn { it.feature.let { it as Trait }.base.toFormula(it.context).calculateFinal().takeIf { it.isNotBlank() } }.apply {
          setHeader("База")
          flexGrow = 0
          minWidth = "6em"
          isSortable = true
        }
        addColumn { it.feature.let { it as Trait }.offenceAlt.toFormula(it.context).calculateFinal().takeIf { it.isNotBlank() } }.apply {
          setHeader("Нападение")
          flexGrow = 0
          minWidth = "6em"
          isSortable = true
        }
        addColumn { it.feature.let { it as Trait }.defenceAlt.toFormula(it.context).calculateFinal().takeIf { it.isNotBlank() } }.apply {
          setHeader("Защита")
          flexGrow = 0
          minWidth = "6em"
          isSortable = true
        }
        addColumn { it.feature.let { it as Trait }.commonAlt.toFormula(it.context).calculateFinal().takeIf { it.isNotBlank() } }.apply {
          setHeader("Общее")
          flexGrow = 0
          minWidth = "6em"
          isSortable = true
        }

        val traits = creature.getAllTraits().sortedWith(
            compareBy<FeatureData> { it.feature.category }
                .thenComparing(compareBy<FeatureData> { it.feature.name })
        ).toList()

        setItems(traits)

        width = "100%"
        height = "100%"
      }

      add(grid)
      add(Button(Icon(VaadinIcon.CLOSE)) {
        close()
      })

      isPadding = false
      isSpacing = false
      width = "100%"
      height = "100%"
    })

    width = "100%"
    height = "100%"
  }
}