package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.setting.SettingObject

interface Hierarchical<T>
    where T : Hierarchical<T>,
          T : SettingObject {
  var base: MutableList<T>
}