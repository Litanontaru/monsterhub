package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.Faction
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object FactionSpace : Space {
  override fun support(obj: Any) = obj is Faction

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val obj = anyObj as Faction

    return listOf(TextField("Ранг").apply {
      value = obj.tier.toString()

      addValueChangeListener {
        update(obj) { obj.tier = it.value.toIntOrNull() ?: 0 }
      }
    })
  }
}