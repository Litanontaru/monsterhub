package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.combobox.ComboBox
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.service.CreatureDataProvider.Companion.ALL_CREATURE_TYPES

object CreatureTypeSpace : Space {
  override fun support(obj: Any) = obj is Creature

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val obj = anyObj as Creature

    return listOf(ComboBox<Pair<String, String>>("Тип").apply {
      setItemLabelGenerator { it.second }

      setItems(ALL_CREATURE_TYPES)

      value = ALL_CREATURE_TYPES.find { it.first == obj.featureType }
      addValueChangeListener {
        update(obj) { obj.featureType = it.value.first }
      }
      width = "100%"
    })
  }
}