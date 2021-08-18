package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.setting.SettingObject
import javax.persistence.CascadeType
import javax.persistence.JoinColumn
import javax.persistence.MappedSuperclass
import javax.persistence.OneToMany

interface FeatureContainerData {
  var features: MutableList<FeatureData>
}