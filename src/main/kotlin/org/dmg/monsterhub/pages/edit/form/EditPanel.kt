package org.dmg.monsterhub.pages.edit.form

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.FeatureData
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
      val configPage = dashBoard(SPACES).apply {
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
      add(dashBoard(SPACES).apply {
        isVisible = !showStats
      })
    }

    height = "100%"
    width = "100%"
    isPadding = false
    isSpacing = false
  }

  private fun dashBoard(spaces: List<List<Space>>) = spaces
      .map { toComponents(it) }
      .filter { it.isNotEmpty() }
      .map { horizontal(it.map { vertical(it) }) }
      .let { vertical(it) }
      .let {
        Scroller(it).apply {
          setSizeFull()
          scrollDirection = Scroller.ScrollDirection.VERTICAL
        }
      }

  private fun toComponents(spaces: List<Space>) = spaces
      .asSequence()
      .filter { it.support(obj) }
      .map { it.use(obj, locator, this::update) }
      .toList()

  private fun vertical(components: List<Component>): Component = when {
    components.size == 1 -> components[0]
    else -> VerticalLayout().apply {
      add(*components.toTypedArray())

      isPadding = false
      isSpacing = false
    }
  }

  private fun horizontal(components: List<Component>): Component = when {
    components.size == 1 -> components[0]
    else -> HorizontalLayout().apply {
      add(*components.toTypedArray())

      width = "100%"
      isPadding = false
    }
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
        listOf(SettingObjectSpace, SkillLikeSpace),
        listOf(PowerTreeSpace),
        listOf(FreeFeatureSpace),
        listOf(FeatureSpace),
        listOf(FeatureContainerSpace),
        listOf(TraitSpace),
        listOf(CreatureSpace),
        listOf(FeatureDataSpace),
        listOf(FeatureContainerDataSpace)
    )
  }
}

