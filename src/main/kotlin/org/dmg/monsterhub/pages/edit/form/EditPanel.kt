package org.dmg.monsterhub.pages.edit.form

import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import org.dmg.monsterhub.data.*
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
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
    SPACES.asSequence()
        .filter { it.support(obj) }
        .forEach { it.use(this, obj, locator, update) }

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

  companion object {
    val SPACES = listOf(
        SettingObjectSpace,
        PowerTreeSpace,
        FreeFeatureSpace,
        FeatureSpace,
        FeatureContainerSpace,
        TraitSpace,
        CreatureSpace,
        FeatureDataSpace,
        FeatureContainerDataSpace
    )
  }
}

object SettingObjectSpace : Space {
  override fun support(obj: Any) = obj is SettingObject

  override fun use(parent: HasComponents, obj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    parent.settingObjectSpace(obj as SettingObject, update)
  }
}

object PowerTreeSpace : Space {
  override fun support(obj: Any): Boolean = obj is Power

  override fun use(parent: HasComponents, obj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    parent.powerTreeSpace(obj as FeatureContainerData, locator, update)
  }
}

object FreeFeatureSpace : Space {
  override fun support(obj: Any) = obj is FreeFeature

  override fun use(parent: HasComponents, obj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    parent.freeFeatureSpace(obj as FreeFeature, update)
  }

}

object FeatureSpace : Space {
  override fun support(obj: Any) = obj is Feature && obj !is Creature

  override fun use(parent: HasComponents, obj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    parent.featureSpace(obj as Feature, update)
  }

}

object FeatureContainerSpace : Space {
  override fun support(obj: Any) = obj is FeatureContainer && obj !is Creature

  override fun use(parent: HasComponents, obj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    parent.featureContainerSpace(obj as FeatureContainer, locator, update)
  }
}

object TraitSpace : Space {
  override fun support(obj: Any) = obj is Trait

  override fun use(parent: HasComponents, obj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    parent.traitSpace(obj as Trait, update)
  }

}

object CreatureSpace : Space {
  override fun support(obj: Any) = obj is Creature

  override fun use(parent: HasComponents, obj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    parent.creatureSpace(obj as Creature, locator, update)
  }

}

object FeatureDataSpace: Space {
  override fun support(obj: Any) = obj is FeatureData

  override fun use(parent: HasComponents, obj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    parent.featureDataSpace(obj as FeatureData, locator, update)
  }
}

object FeatureContainerDataSpace: Space {
  override fun support(obj: Any) = obj is FeatureContainerData && obj !is Power

  override fun use(parent: HasComponents, obj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
    parent.featureContainerDataSpace(obj as FeatureContainerData, locator, update)
  }
}