package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.setting.SettingObject
import javax.persistence.*

@Entity
class Creature : SettingObject(), FeatureContainerData, Hierarchical<Creature> {
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "Base_Creature",
      joinColumns = [JoinColumn(name = "creature_id")],
      inverseJoinColumns = [JoinColumn(name = "base_id")]
  )
  override var base: MutableList<Creature> = mutableListOf()

  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "feature_container_id")
  override var features: MutableList<FeatureData> = mutableListOf()
}