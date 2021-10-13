package org.dmg.monsterhub.data.setting

import javax.persistence.Entity

@Entity
class Folder : SettingObject() {
  companion object {
    val FOLDER = "FOLDER"
  }
}