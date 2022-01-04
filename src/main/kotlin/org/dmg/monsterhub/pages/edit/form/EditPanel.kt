package org.dmg.monsterhub.pages.edit.form

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.FeatureDataDesignation
import org.dmg.monsterhub.data.WeaponAttack
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.form.space.*
import org.dmg.monsterhub.repository.update

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
      tabPages.forEach { (tab, panel) -> panel.isVisible = tab.label == locator.config.selectedTab }

      add(*tabPages.values.toTypedArray())
      add(Tabs(*tabPages.keys.toTypedArray()).apply {
        selectedTab = tabPages.entries.find { it.value.isVisible }?.key

        addSelectedChangeListener {
          tabPages.values.forEach { it.isVisible = false }

          tabPages[it.selectedTab]
              ?.isVisible = true
          locator.config.selectedTab = it.selectedTab.label
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

  private fun update(obj: Any, action: () -> Unit): Any =
      locator.transactionService {
        action()
        when (obj) {
          is SettingObject -> {
            locator.objectManagerService
                .update(obj)
                .also {
                  if (obj == this.obj) {
                    onUpdate?.let { it() }
                  }
                }
          }
          is FeatureData -> {
            locator.featureDataRepository.update(obj)
          }
          is WeaponAttack -> {
            locator.weaponAttackRepository.update(obj)
          }
          is FeatureDataDesignation -> {
            locator.featureDataDesignationRepository.update(obj)
          }
          else -> throw UnsupportedOperationException()
        }
      }

  companion object {
    val SPACES = listOf(
        listOf(SettingObjectSpace, SkillLikeSpace, PowerEffectSpace, FreeFeatureTypeSpace, CreatureTypeSpace, FreeFeatureSpace, RateSpace, EditableRateSpace, FactionSpace),
        listOf(TreeSpace2),
        listOf(DescriptionSpace),
        listOf(FeatureSpace),
        listOf(OneWeaponAttackSpace),
        listOf(ArmorSpace),
        listOf(FeatureContainerSpace),
        listOf(TraitSpace),
        listOf(FeatureDataSpace),
        listOf(FeatureContainerDataSpace),
        listOf(WeaponAttackSpace),
        listOf(SettingBaseSpace),
        listOf(TraitApplicationSpace)
    )

    val STAT_SPACES = listOf(
        listOf(SuperioritySpace, CreatureSizeSpace),
        listOf(CreatureTraitSpace),
        listOf(CreatureAttackSpace),
        listOf(GameCharacterAttackSpace),
        listOf(CreatureDefenceSpace),
        listOf(GameCharacterDefence),
        listOf(CreatureSkillSpace),
        listOf(CreatureSpeedSpace)
    )

    val TABS = mapOf(
        "Конфигурация" to SPACES,
        "Статистика" to STAT_SPACES
    )
  }
}

