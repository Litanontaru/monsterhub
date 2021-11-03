package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
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
        TextField("База").apply {
          value = values[0].toString()
          isReadOnly = true
          width = "100%"
        },
        HorizontalLayout().apply {
          add(
              TextField("Нападение").apply {
                value = values[1].toString()
                isReadOnly = true
                width = "100%"
              },
              TextField("Защита").apply {
                value = values[2].toString()
                isReadOnly = true
                width = "100%"
              },
              TextField("Общее").apply {
                value = values[3].toString()
                isReadOnly = true
                width = "100%"
              }
          )

          isPadding = false
          width = "100%"
        }
    )
  }
}