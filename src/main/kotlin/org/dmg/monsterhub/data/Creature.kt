package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.Trait.Companion.TRAIT
import org.dmg.monsterhub.data.meta.FeatureContainerItem
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

  override fun meta(): List<FeatureContainerItem> = META

  fun getAll(type: String): Sequence<FeatureData> {
    return base
        .distinct()
        .map { it.getAll(type) }
        .fold(my(type), ::combine)
  }

  private fun my(type: String): Sequence<FeatureData> = features
      .asSequence()
      .filter { it.feature.featureType == type }

  private fun combine(left: Sequence<FeatureData>, right: Sequence<FeatureData>): Sequence<FeatureData> {
    val names = left.map { it.feature.name }.toSet()
    val groups = left.mapNotNull { it.feature.selectionGroup }.toSet()

    return left + right.filter { it.feature.name !in names && (it.feature.selectionGroup !in groups) }
  }

  fun getAllTraits(): Sequence<FeatureData> = getAll(TRAIT)

  fun getAllTraits(category: String, vararg categories: String) = getAllTraits((sequenceOf(category) + categories).toSet())

  fun getAllTraits(categories: Set<String>) = getAllTraits().filter { it.feature.category in categories || it.feature.name in categories }

  override fun rate(): Decimal = CreatureService.superiority(this).value.toBigDecimal().toDecimal()

  companion object {
    const val CREATURE = "CREATURE"

    val META = listOf(
        FeatureContainerItem().apply {
          name = "Черты"
          featureType = TRAIT

        },
        FeatureContainerItem().apply {
          name = "Оружие"
          featureType = Weapon.WEAPON

        },
        FeatureContainerItem().apply {
          name = "Броня"
          featureType = Armor.ARMOR
          onlyOne = true
        },
        FeatureContainerItem().apply {
          name = "Способности"
          featureType = Skill.SKILL
        },
        FeatureContainerItem().apply {
          name = "Перки"
          featureType = Perk.PERK
        },
        FeatureContainerItem().apply {
          name = "Силы"
          featureType = Power.POWER
          allowHidden = true
        }
    )
  }
}