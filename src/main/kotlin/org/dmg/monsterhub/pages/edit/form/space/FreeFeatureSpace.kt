package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.combobox.ComboBox
import org.dmg.monsterhub.data.FreeFeature
import org.dmg.monsterhub.data.meta.FreeFeatureType
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object FreeFeatureSpace : Space {
  override fun support(obj: Any) = obj is FreeFeature

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val obj = anyObj as FreeFeature

    return listOf(ComboBox<FreeFeatureType>("Тип").apply {
      setItemLabelGenerator { it.display }

      setItems(locator.freeFeatureDataProvider.supportedTypes)

      value = locator.freeFeatureDataProvider.supportedTypes.find { it.name == obj.featureType }
      addValueChangeListener {
        update(obj) { obj.featureType = it.value.name }
      }
      width = "100%"
    })
  }
}