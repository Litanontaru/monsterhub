package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object SettingObjectSpace : Space {
  override fun support(obj: Any) = obj is SettingObject

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val obj = anyObj as SettingObject

    return listOf(TextField("Название").apply {
      value = obj.name
      addValueChangeListener {
        update(obj) { obj.name = it.value }
      }
      width = "100%"
    })
  }
}