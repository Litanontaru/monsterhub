package org.dmg.monsterhub.data.setting

import org.dmg.monsterhub.data.DBObject
import org.dmg.monsterhub.data.Named
import javax.persistence.*

@Entity
class Setting : DBObject(), Named {
  override var name: String = ""

  @ManyToMany
  @JoinTable(
      name = "base_setting",
      joinColumns = [JoinColumn(name = "setting_id")],
      inverseJoinColumns = [JoinColumn(name = "base_id")]
  )
  var base: MutableList<Setting> = mutableListOf()

  var description: String = ""
}