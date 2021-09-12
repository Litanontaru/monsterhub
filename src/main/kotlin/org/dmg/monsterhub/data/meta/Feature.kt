package org.dmg.monsterhub.data.meta

import org.dmg.monsterhub.data.setting.SettingObject
import org.hibernate.annotations.Type
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
open class Feature : SettingObject(), FeatureContainer {
  open lateinit var featureType: String

  open var rate: String? = null

  @Type(type = "text")
  open var description: String = ""

  open var x: NumberOption = NumberOption.NONE
  open var y: NumberOption = NumberOption.NONE
  open var z: NumberOption = NumberOption.NONE

  @ElementCollection
  @CollectionTable(name = "feature_designation", joinColumns = [JoinColumn(name = "feature_id")])
  @Column(name = "designation")
  open var designations: List<String> = mutableListOf()

  open var selectionGroup: String? = null
  open var category: String = ""

  @OneToMany(orphanRemoval = true, cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
  @JoinColumn(name = "feature_id")
  override val containFeatureTypes: MutableList<FeatureContainerItem> = mutableListOf()
}