package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.Trait.Companion.TRAIT
import javax.persistence.*

@Entity
class Creature : ContainerData(), Hierarchical<Creature> {
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "Base_Creature",
      joinColumns = [JoinColumn(name = "creature_id")],
      inverseJoinColumns = [JoinColumn(name = "base_id")]
  )
  override var base: MutableList<Creature> = mutableListOf()

  fun getAllTraits(): Sequence<FeatureData> = base
      .distinct()
      .map { it.getAllTraits() }
      .fold(myTraits(), ::combine)

  fun myTraits(): Sequence<FeatureData> = features
      .asSequence()
      .filter { it.feature.featureType == TRAIT }

  private fun combine(left: Sequence<FeatureData>, right: Sequence<FeatureData>): Sequence<FeatureData> {
    val names = left.map { it.feature.name }.toSet()
    val groups = left.mapNotNull { it.feature.selectionGroup }.toSet()

    return left + right.filter { it.feature.name !in names && (it.feature.selectionGroup !in groups) }
  }
}