package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.combobox.ComboBox
import org.dmg.monsterhub.data.FreeFeature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.service.FreeFeatureDataProvider

object FreeFeatureSpace : Space {
  override fun support(obj: Any) = obj is FreeFeature

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit): List<Component> {
    val obj = anyObj as FreeFeature

    return listOf(ComboBox<String>("Тип").apply {
      setItems(FreeFeatureDataProvider.MY_TYPES)

      value = obj.featureType
      addValueChangeListener {
        update(obj) { obj.featureType = it.value }
      }
      width = "100%"
    })
  }
}