package org.dmg.monsterhub.pages

import com.vaadin.flow.component.html.Div
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.service.SettingService

@Route("setting2/:settingId")
class SettingView2(
    private val settingService: SettingService,
    private val objectTreeDataProviderService2: ObjectTreeDataProviderService2
) : Div(), BeforeEnterObserver, HasDynamicTitle {

  private var initialized = false
  private lateinit var setting: Setting

  init {
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
    if (!initialized || settingId != setting.id) {
      initialized = true

      setting = settingService.get(settingId)

      add(SettingObjectTree(setting, objectTreeDataProviderService2))
    }
  }

  override fun getPageTitle(): String = "MonsterHub. ${setting.name}"
}