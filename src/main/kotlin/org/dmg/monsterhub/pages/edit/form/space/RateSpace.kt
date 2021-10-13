package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.Faction
import org.dmg.monsterhub.data.FreeFeature
import org.dmg.monsterhub.data.PowerEffect
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object RateSpace : Space {
  override fun support(obj: Any) = obj is SettingObject && obj !is FreeFeature && obj !is PowerEffect && obj !is Faction

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val obj = anyObj as SettingObject

    return listOf(TextField("Показатель").apply {
      value = obj.rate().toString()
      width = "5em"
      isReadOnly = true
    })
  }
}