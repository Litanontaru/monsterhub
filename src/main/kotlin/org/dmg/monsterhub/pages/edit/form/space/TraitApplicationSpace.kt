package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.Trait
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object TraitApplicationSpace : Space {
  override fun support(obj: Any): Boolean = obj is FeatureData && obj.feature is Trait

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val featureData = anyObj as FeatureData
    val trait = featureData.feature as Trait
    val values = trait.formulas(featureData.context).map { it.calculateFinal().toInt() }.toList()

    return listOf(
        Label("Показатели черты"),
        HorizontalLayout().apply {
          val offence = VerticalLayout().apply {
            add(TextField("Напападение").apply {
              value = values[0].toString()
              isReadOnly = true
              width = "100%"
            })

            add(TextField().apply {
              value = if (values[1] == 0) "" else values[1].toString()
              isReadOnly = true
              width = "100%"
            })
            width = "100%"
            isPadding = false
            isSpacing = false
          }
          val defence = VerticalLayout().apply {
            add(TextField("Защита").apply {
              value = values[2].toString()
              isReadOnly = true
              width = "100%"
            })

            add(TextField().apply {
              value = if (values[3] == 0) "" else values[3].toString()
              isReadOnly = true
              width = "100%"
            })

            width = "100%"
            isPadding = false
            isSpacing = false
          }

          val common = VerticalLayout().apply {
            add(TextField("Общее").apply {
              value = values[4].toString()
              isReadOnly = true
              width = "100%"
            })

            add(TextField().apply {
              value = if (values[5] == 0) "" else values[5].toString()
              isReadOnly = true
              width = "100%"
            })

            width = "100%"
            isPadding = false
            isSpacing = false
          }

          add(offence, defence, common)
          expand(offence, defence, common)

          width = "100%"
          isPadding = false
        }
    )
  }
}