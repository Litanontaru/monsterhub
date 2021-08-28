package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object SettingObjectSpace : Space {
  override fun support(obj: Any) = obj is SettingObject

  override fun use(parent: HasComponents, anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    val obj = anyObj as SettingObject

    parent.add(HorizontalLayout().apply {
      add(TextField("Название").apply {
        value = obj.name
        addValueChangeListener {
          update(obj) { obj.name = it.value }
        }
        width = "100%"
      })

      add(TextField("Показатель").apply {
        value = obj.rate().toString()
        width = "5em"
        isReadOnly = true
      })

      width = "100%"
    })
  }
}