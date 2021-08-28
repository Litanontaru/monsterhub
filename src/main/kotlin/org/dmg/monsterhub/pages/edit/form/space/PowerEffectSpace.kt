package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.combobox.ComboBox
import org.dmg.monsterhub.data.PowerEffect
import org.dmg.monsterhub.data.PowerRateCalculator
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object PowerEffectSpace : Space {
  override fun support(obj: Any) = obj is PowerEffect

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit): List<Component> {
    val obj = anyObj as PowerEffect

    return listOf(ComboBox<PowerRateCalculator>("Расчёт Силы").apply {
      setItems(PowerRateCalculator.values().toList())
      setItemLabelGenerator { it.display }

      value = obj.calculator

      addValueChangeListener {
        update(obj) { obj.calculator = it.value }
      }
    })
  }
}