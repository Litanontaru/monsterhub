package org.dmg.monsterhub.data.meta

import org.dmg.monsterhub.data.setting.SettingObject
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
open class Feature : SettingObject() {
  open lateinit var featureType: String

  open var description: String = ""

  open var x: NumberOption = NumberOption.NONE
  open var y: NumberOption = NumberOption.NONE
  open var z: NumberOption = NumberOption.NONE

  @ElementCollection
  @CollectionTable(name = "feature_designation", joinColumns = [JoinColumn(name = "feature_id")])
  @Column(name = "designation")
  open val designations: List<String> = mutableListOf()

  open val selectionGroup: String? = null
  open val category: String = ""
}