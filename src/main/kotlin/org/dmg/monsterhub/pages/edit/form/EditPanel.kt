package org.dmg.monsterhub.pages.edit.form

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.WeaponAttack
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.form.space.*

class EditPanel(
    private val obj: Any,
    private val locator: ServiceLocator,
    private val onUpdate: (() -> Unit)? = null
) : VerticalLayout() {
  init {
    val tabs = TABS
        .mapValues { dashBoard(it.value) }
        .filter { it.value != null }
    if (tabs.size == 1) {
      add(tabs.values.first()!!)
    } else {
      val tabPages = tabs.mapKeys { Tab(it.key) }.mapValues { it.value!! }
      tabPages.values.drop(1).forEach { it.isVisible = false }

      add(*tabPages.values.toTypedArray())
      add(Tabs(*tabPages.keys.toTypedArray()).apply {
        selectedTab = tabPages.entries.find { it.value.isVisible }?.key

        addSelectedChangeListener {
          tabPages.values.forEach { it.isVisible = false }
          val layout = tabPages[it.selectedTab]

          layout?.isVisible = true
        }
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
      .takeIf { it.isNotEmpty() }
      ?.let { vertical(it) }
      ?.let {
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
      is FeatureData -> {
        if (obj.deleteOnly) {
          locator.featureDataRepository.delete(obj)
        } else {
          locator.featureDataRepository.save(obj)
        }
      }
      is WeaponAttack -> {
        if (obj.deleteOnly) {
          locator.weaponAttackRepository.delete(obj)
        } else {
          locator.weaponAttackRepository.save(obj)
        }
      }
    }
    if (onUpdate != null && obj == this.obj) {
      onUpdate!!()
    }
  }

  companion object {
    val SPACES = listOf(
        listOf(SettingObjectSpace, SkillLikeSpace, PowerEffectSpace, RateSpace, EditableRateSpace),
        listOf(PowerTreeSpace),
        listOf(FreeFeatureSpace),
        listOf(DescriptionSpace),
        listOf(FeatureSpace),
        listOf(OneWeaponAttackSpace),
        listOf(ArmorSpace),
        listOf(FeatureContainerSpace),
        listOf(TraitSpace),
        listOf(CreatureSpace),
        listOf(FeatureDataSpace),
        listOf(WeaponAttackSpace),
        listOf(FeatureContainerDataSpace)
    )

    val STAT_SPACES = listOf(
        listOf(SuperioritySpace, CreatureSizeSpace, CreatureTraitSpace),
        listOf(CreatureAttackSpace),
        listOf(CreatureDefence),
        listOf(CreatureSkillSpace),
        listOf(CreatureSpeedSpace)
    )

    val TABS = mapOf(
        "Конфигурация" to SPACES,
        "Статистика" to STAT_SPACES
    )
  }
}

