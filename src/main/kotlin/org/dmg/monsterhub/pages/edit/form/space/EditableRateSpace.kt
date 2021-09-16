package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.FreeFeature
import org.dmg.monsterhub.data.PowerEffect
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object EditableRateSpace : Space {
  override fun support(obj: Any) = obj is FreeFeature || obj is PowerEffect

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val obj = anyObj as Feature

    return listOf(TextField("Показатель").apply {
      value = obj.rate ?: ""
      addValueChangeListener {
        update(obj) { obj.rate = it.value.takeIf { it.isNotBlank() } }
      }
    })
  }
}