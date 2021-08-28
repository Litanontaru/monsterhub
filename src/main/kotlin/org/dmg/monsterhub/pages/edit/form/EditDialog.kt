package org.dmg.monsterhub.pages.edit.form

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.dmg.monsterhub.pages.ObjectTreeDataProvider
import org.dmg.monsterhub.pages.edit.data.ObjectFinderDataProviderForSetting
import org.dmg.monsterhub.repository.FeatureContainerItemRepository
import org.dmg.monsterhub.repository.FeatureDataDesignationRepository
import org.dmg.monsterhub.service.FeatureContainerServiceLocator
import org.dmg.monsterhub.service.FeatureDataRepository

class EditDialog(
    private val obj: Any,
    private val data: ObjectTreeDataProvider,
    private val fiderData: ObjectFinderDataProviderForSetting,
    private val featureDataRepository: FeatureDataRepository,
    private val featureContainerItemRepository: FeatureContainerItemRepository,
    private val featureDataDesignationRepository: FeatureDataDesignationRepository,
    private val featureContainerServiceLocator: FeatureContainerServiceLocator,
    private val onUpdate: (() -> Unit)? = null
): Dialog() {
  init {
    add(VerticalLayout().apply {
      add(EditPanel(
          obj,
          data,
          fiderData,
          featureDataRepository,
          featureContainerItemRepository,
          featureDataDesignationRepository,
          featureContainerServiceLocator,
          false,
          onUpdate
      ))
      add(Button(Icon(VaadinIcon.CLOSE)) {
        close()
      })

      width = "100%"
      height = "100%"
      isPadding = false
      isSpacing = false
    })

    width = "100%"
    height = "100%"
  }
}