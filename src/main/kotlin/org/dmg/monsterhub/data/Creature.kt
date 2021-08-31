package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.Trait.Companion.TRAIT
import org.dmg.monsterhub.service.CreatureService
import org.dmg.monsterhub.service.Decimal
import org.dmg.monsterhub.service.toDecimal
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

  fun getAllTraits(category: String, vararg categories: String) = getAllTraits((sequenceOf(category) + categories).toSet())

  fun getAllTraits(categories: Set<String>) = getAllTraits().filter { it.feature.category in categories || it.feature.name in categories }

  override fun rate(): Decimal = CreatureService.superiority(this).value.toBigDecimal().toDecimal()
}