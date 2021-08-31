package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.textfield.TextArea
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object DescriptionSpace : Space {
  override fun support(obj: Any) = obj is Feature && obj !is Creature

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit): List<Component> {
    val obj = anyObj as Feature

    return listOf(TextArea("Описание").apply {
      value = obj.description
      addValueChangeListener {
        update(obj) { obj.description = it.value }
      }
      width = "100%"
    })
  }
}