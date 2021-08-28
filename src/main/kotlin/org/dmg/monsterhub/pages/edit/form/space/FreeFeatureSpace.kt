package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.FreeFeature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.service.FreeFeatureDataProvider

object FreeFeatureSpace : Space {
  override fun support(obj: Any) = obj is FreeFeature

  override fun use(parent: HasComponents, anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    val obj = anyObj as FreeFeature

    parent.add(HorizontalLayout().apply {
      add(ComboBox<String>("Тип").apply {
        setItems(FreeFeatureDataProvider.MY_TYPES)

        value = obj.featureType
        addValueChangeListener {
          update(obj) { obj.featureType = it.value }
        }
        width = "100%"
      })

      add(TextField("Показатель").apply {
        value = obj.rate ?: ""
        addValueChangeListener {
          update(obj) { obj.rate = it.value.takeIf { it.isNotBlank() } }
        }
      })

      width = "100%"
    })
  }
}