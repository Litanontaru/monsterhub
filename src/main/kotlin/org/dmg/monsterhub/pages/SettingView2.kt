package org.dmg.monsterhub.pages

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.*
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.pages.edit.data.ObjectFinderDataProviderService
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.form.EditPanel
import org.dmg.monsterhub.pages.edit.form.EditPanelConfig
import org.dmg.monsterhub.repository.*
import org.dmg.monsterhub.service.*

@Route("setting2/:settingId/edit/:objId")
class SettingView2(
    featureService: FeatureService,
    dependencyAnalyzer: DependencyAnalyzer,

    private val dataProviders: List<SettingObjectDataProvider>,
    private val settingService: SettingService,
    private val objectFinderDataProviderService: ObjectFinderDataProviderService,
    private val featureDataRepository: FeatureDataRepository,
    private val featureContainerItemRepository: FeatureContainerItemRepository,
    private val featureDataDesignationRepository: FeatureDataDesignationRepository,
    private val powerEffectRepository: PowerEffectRepository,
    private val weaponAttackRepository: WeaponAttackRepository,
    private val weaponRepository: WeaponRepository,
    private val settingRepository: SettingRepository,
    private val transactionService: TransactionService
) : Div(), BeforeEnterObserver, HasDynamicTitle {

  private val config: EditPanelConfig = EditPanelConfig("Конфигурация")

  private val data = ObjectTreeDataProvider2(featureService)
  private var rightPanel: VerticalLayout

  private val manager = object : ObjectManagerService {
    override fun create(featureType: String): SettingObject =
        dataProviders
            .first { it.supportType(featureType) }
            .create()

    override fun update(settingObject: SettingObject): SettingObject =
        dataProviders
            .first { settingObject::class.java.isAssignableFrom(it.objectClass) }
            .save(settingObject)
  }

  init {
    val leftPanel = SettingObjectTree(data, dataProviders, settingRepository, dependencyAnalyzer) { obj ->
      clickAndHistory(obj)
    }
    rightPanel = VerticalLayout().apply {
      height = "100%"
      width = "100%"
      isPadding = false
      isSpacing = false
    }

    add(HorizontalLayout().apply {
      add(leftPanel, rightPanel)

      setSizeFull()
      isPadding = true
    })

    height = "100%"
    width = "100%"
  }

  private fun SettingObjectTreeNode.toObject(): SettingObject? {
    return dataProviders
        .find { it.supportType(featureType) }
        ?.getById(id)
  }

  private fun clickAndHistory(obj: SettingObjectTreeNode) {
    obj.toObject()
        ?.let {
          click(it)
          history(it)
        }
  }

  private fun click(item: SettingObject) {
    rightPanel.removeAll()

    rightPanel.add(EditPanel(
        item,
        ServiceLocator(
            data.setting!!,

            objectFinderDataProviderService,
            manager,
            featureDataRepository,
            featureContainerItemRepository,
            featureDataDesignationRepository,
            powerEffectRepository,
            weaponRepository,
            weaponAttackRepository,
            settingRepository,

            transactionService,

            config
        ).also { it.refreshSettings() }
    ))
  }

  private fun history(item: SettingObject) {
    val routeConfiguration = RouteConfiguration.forSessionScope()
    val parameters = RouteParameters(mutableMapOf(
        "settingId" to data.setting!!.id.toString(),
        "objId" to item.id.toString()
    ))
    val url = routeConfiguration.getUrl(SettingView2::class.java, parameters)
    UI.getCurrent().page.history.pushState(null, url)
  }

  override fun beforeEnter(event: BeforeEnterEvent?) {
    if (event != null) {
      set(event.routeParameters["settingId"].get().toLong())
//      event.routeParameters["objId"].ifPresent { select(it.toLong()) }
    }
  }

  private fun set(settingId: Long) {
    if (data.setting == null || settingId != data.setting!!.id) {

      data.setting = settingService.get(settingId)
      data.refreshAll()
    }
  }

  override fun getPageTitle(): String = "MonsterHub. ${data.setting?.name ?: ""}"
}