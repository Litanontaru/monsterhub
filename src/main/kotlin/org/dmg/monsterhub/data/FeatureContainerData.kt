package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.setting.SettingObject
import javax.persistence.CascadeType
import javax.persistence.JoinColumn
import javax.persistence.MappedSuperclass
import javax.persistence.OneToMany

@MappedSuperclass
open class FeatureContainerData: SettingObject() {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "feature_container_id")
    var features: MutableList<FeatureData> = mutableListOf()
}