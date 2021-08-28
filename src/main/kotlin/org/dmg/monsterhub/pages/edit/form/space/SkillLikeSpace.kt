package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.combobox.ComboBox
import org.dmg.monsterhub.data.SkillLike
import org.dmg.monsterhub.data.SkillType
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object SkillLikeSpace : Space {
  override fun support(obj: Any) = obj is SkillLike

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit): List<Component> {
    val parent = mutableListOf<Component>()
    val obj = anyObj as SkillLike

    parent.add(ComboBox<SkillType>("Тип навыка").apply {
      setItems(SkillType.values().toList())
      setItemLabelGenerator { it.display }

      value = obj.skillType

      addValueChangeListener {
        update(obj) { obj.skillType = it.value }
      }
    })

    return parent
  }
}