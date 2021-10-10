package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.meta.FreeFeatureType
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object FreeFeatureTypeSpace : Space {
  override fun support(obj: Any): Boolean = obj is FreeFeatureType

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val obj = anyObj as FreeFeatureType

    return listOf(TextField("Отображаемое название").apply {
      value = obj.display
      addValueChangeListener {
        update(obj) { obj.display = it.value }
      }
      width = "100%"
    })
  }
}