package org.dmg.monsterhub.pages.edit.form

import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import org.dmg.monsterhub.data.*
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.form.space.*

class EditPanel(
    private val obj: Any,
    private val locator: ServiceLocator,
    var showStats: Boolean,
    private val onUpdate: (() -> Unit)? = null
) : VerticalLayout() {
  init {
    if (obj is Creature) {
      val configPage = Scroller(
          VerticalLayout().also { configSpace(obj, this::update) }
      ).apply {
        setSizeFull()
        scrollDirection = Scroller.ScrollDirection.VERTICAL
        isVisible = !showStats
      }
      val statsPage = Scroller(CreatureStatsSpace(obj)).apply {
        setSizeFull()
        scrollDirection = Scroller.ScrollDirection.VERTICAL
        isVisible = showStats
      }
      val pages = listOf(configPage, statsPage)

      val configTab = Tab("Конфигурация")
      val statsTab = Tab("Статистика")

      val tabPages = mapOf(configTab to configPage, statsTab to statsPage)

      add(configPage, statsPage)
      add(Tabs(configTab, statsTab).apply {
        selectedTab = tabPages.entries.find { it.value.isVisible }?.key

        addSelectedChangeListener {
          pages.forEach { it.isVisible = false }
          val layout = tabPages[it.selectedTab]

          layout?.isVisible = true

          showStats = layout == statsPage
        }
      })
    } else {
      add(Scroller(
          VerticalLayout().also { configSpace(obj, this::update) }
      ).apply {
        setSizeFull()
        scrollDirection = Scroller.ScrollDirection.VERTICAL
        isVisible = !showStats
      })
    }

    height = "100%"
    width = "100%"
    isPadding = false
    isSpacing = false
  }

  private fun VerticalLayout.configSpace(obj: Any, update: (Any, () -> Unit) -> Unit) {
    if (obj is SettingObject) {
      settingObjectSpace(obj, update)
    }

    if (obj is Power) {
      powerTreeSpace(obj, locator, update)
    }

    if (obj is FreeFeature) {
      freeFeatureSpace(obj, update)
    }

    if (obj is Feature && obj !is Creature) {
      featureSpace(obj, update)
    }

    if (obj is FeatureContainer && obj !is Creature) {
      featureContainerSpace(obj, locator, update)
    }

    if (obj is Trait) {
      traitSpace(obj, update)
    }

    if (obj is Creature) {
      creatureSpace(obj, locator, update)
    }

    if (obj is FeatureData) {
      featureDataSpace(obj, locator, update)
    }

    if (obj is FeatureContainerData && obj !is Power) {
      featureContainerDataSpace(obj, locator, update)
    }

    height = "100%"
    width = "100%"
    isPadding = false
    isSpacing = false
  }

  private fun update(obj: Any, action: () -> Unit) {
    action()
    when (obj) {
      is SettingObject -> locator.data.update(obj)
      is FeatureData -> locator.featureDataRepository.save(obj)
    }
    if (onUpdate != null && obj == this.obj) {
      onUpdate!!()
    }
  }
}