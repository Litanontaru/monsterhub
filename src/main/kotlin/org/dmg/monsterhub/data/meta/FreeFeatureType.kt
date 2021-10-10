package org.dmg.monsterhub.data.meta

import org.dmg.monsterhub.data.setting.SettingObject
import javax.persistence.Entity

@Entity
class FreeFeatureType : SettingObject() {
  var display: String = ""

  companion object {
    val FREE_FEATURE_TYPE = "FREE_FEATURE_TYPE"
  }
}