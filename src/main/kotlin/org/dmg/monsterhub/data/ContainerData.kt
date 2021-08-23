package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.Feature
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
open class ContainerData : Feature(), FeatureContainerData {
  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "feature_container_id")
  override var features: MutableList<FeatureData> = mutableListOf()
}