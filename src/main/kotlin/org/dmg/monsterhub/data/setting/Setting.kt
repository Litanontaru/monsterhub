package org.dmg.monsterhub.data.setting

import org.dmg.monsterhub.data.WithDescription
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany

@Entity
class Setting : SettingObject(), WithDescription {
  @ManyToMany
  @JoinTable(
      name = "base_setting",
      joinColumns = [JoinColumn(name = "setting_id")],
      inverseJoinColumns = [JoinColumn(name = "base_id")]
  )
  var base: MutableList<Setting> = mutableListOf()

  override var description: String = ""
}