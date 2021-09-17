package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.textfield.TextArea
import org.dmg.monsterhub.data.WithDescription
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object DescriptionSpace : Space {
  override fun support(obj: Any) = obj is WithDescription

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val obj = anyObj as WithDescription

    return listOf(TextArea("Описание").apply {
      value = obj.description
      addValueChangeListener {
        update(obj) { obj.description = it.value }
      }
      width = "100%"
    })
  }
}