package org.dmg.monsterhub.data.meta

import org.dmg.monsterhub.data.setting.SettingObject
import javax.persistence.*

@Entity
class Feature: SettingObject() {
    lateinit var featureType: String

    var description: String = ""

    var x: NumberOption = NumberOption.NONE
    var y: NumberOption = NumberOption.NONE
    var z: NumberOption = NumberOption.NONE

    @ElementCollection
    @CollectionTable(name = "feature_designation", joinColumns = [JoinColumn(name = "feature_id")])
    @Column(name = "designation")
    val designations: List<String> = mutableListOf()

    val selectionGroup: String? = null
    val category: String = ""


}