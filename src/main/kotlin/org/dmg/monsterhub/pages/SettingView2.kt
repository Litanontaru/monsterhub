package org.dmg.monsterhub.pages

import com.vaadin.flow.component.html.Div
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import org.dmg.monsterhub.service.FeatureService
import org.dmg.monsterhub.service.SettingObjectDataProvider
import org.dmg.monsterhub.service.SettingService

@Route("setting2/:settingId")
class SettingView2(
    featureService: FeatureService,
    dataProviders: List<SettingObjectDataProvider>,

    private val settingService: SettingService
) : Div(), BeforeEnterObserver, HasDynamicTitle {

  private val data = ObjectTreeDataProvider2(featureService)

  init {
    add(SettingObjectTree(data, dataProviders) { })

    height = "100%"
    width = "100%"
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