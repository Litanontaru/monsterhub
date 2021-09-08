package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.Armor
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object ArmorSpace : Space {
  override fun support(obj: Any) = obj is Armor

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit): List<TextField> {
    val obj = anyObj as Armor
    return listOf(
        TextField("Сильная броня").apply {
          this.value = obj.strong.toString()
          addValueChangeListener {
            update(obj) { obj.strong = it.value.toIntOrNull() ?: 0 }
          }
        },
        TextField("Стандартная броня").apply {
          this.value = obj.standard.toString()
          addValueChangeListener {
            update(obj) { obj.standard = it.value.toIntOrNull() ?: 0 }
          }
        },
        TextField("Слабая броня").apply {
          this.value = obj.weak.toString()
          addValueChangeListener {
            update(obj) { obj.weak = it.value.toIntOrNull() ?: 0 }
          }
        }
    )
  }
}