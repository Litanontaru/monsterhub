package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.setting.SettingObject
import javax.persistence.JoinColumn
import javax.persistence.MappedSuperclass
import javax.persistence.OneToMany

@MappedSuperclass
class Item : SettingObject(), FeatureContainerData {
  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "feature_container_id")
  override var features: MutableList<FeatureData> = mutableListOf()
}