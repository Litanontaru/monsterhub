package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.Trait.Companion.TRAIT
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.service.CreatureService
import org.dmg.monsterhub.service.Decimal
import org.dmg.monsterhub.service.toDecimal
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
open class Creature : ContainerData(), Hierarchical<Creature> {
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "Base_Creature",
      joinColumns = [JoinColumn(name = "creature_id")],
      inverseJoinColumns = [JoinColumn(name = "base_id")]
  )
  override var base: MutableList<Creature> = mutableListOf()

  override fun meta(): List<FeatureContainerItem> = when (featureType) {
    CREATURE_RACE, CREATURE_RACE_TEMPLATE -> CREATURE_RACE_META
    CREATURE, CREATURE_REPRESENTATIVE -> CREATURE_META
    else -> throw UnsupportedOperationException("Unknown creature type $featureType")
  }

  //--------------------------------------------------------------------------------------------------------------------

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
    val names = left
        .filter {
          when (val feature = it.feature) {
            is Trait -> feature.overriding
            else -> false
          }
        }
        .map { it.feature.name }.toSet()
    val groups = left.mapNotNull { it.feature.selectionGroup }.toSet()

    return left + right.filter { it.feature.name !in names && (it.feature.selectionGroup !in groups) }
  }

  fun getAllWithCreature(type: String): Sequence<Pair<Creature, FeatureData>> {
    return base
        .distinct()
        .map { it.getAllWithCreature(type) }
        .fold(myWithCreature(type), ::combineWithCreature)
  }

  private fun myWithCreature(type: String): Sequence<Pair<Creature, FeatureData>> = features
      .asSequence()
      .filter { it.feature.featureType == type }
      .map { this to it }

  private fun combineWithCreature(left: Sequence<Pair<Creature, FeatureData>>, right: Sequence<Pair<Creature, FeatureData>>): Sequence<Pair<Creature, FeatureData>> {
    val names = left
        .filter {
          when (val feature = it.second.feature) {
            is Trait -> feature.overriding
            else -> false
          }
        }
        .map { it.second.feature.name }.toSet()
    val groups = left.mapNotNull { it.second.feature.selectionGroup }.toSet()

    return left + right.filter { it.second.feature.name !in names && (it.second.feature.selectionGroup !in groups) }
  }

  //--------------------------------------------------------------------------------------------------------------------

  fun getAllTraits(): Sequence<FeatureData> = getAll(TRAIT)

  fun getAllTraits(category: String, vararg categories: String) = getAllTraits((sequenceOf(category) + categories).toSet())

  fun getAllTraits(categories: Set<String>) = getAllTraits().filter { it.feature.category in categories || it.feature.name in categories }

  //--------------------------------------------------------------------------------------------------------------------

  override fun rate(): Decimal = CreatureService.superiority(this).value.toBigDecimal().toDecimal()

  companion object {
    const val CREATURE = "CREATURE"
    const val CREATURE_RACE_TEMPLATE = "CREATURE_RACE_TEMPLATE"
    const val CREATURE_RACE = "CREATURE_RACE"
    const val CREATURE_REPRESENTATIVE = "CREATURE_REPRESENTATIVE"

    val CREATURE_TYPES = listOf(
        CREATURE,
        CREATURE_RACE_TEMPLATE,
        CREATURE_RACE,
        CREATURE_REPRESENTATIVE
    )

    val CREATURE_META = listOf(
        FeatureContainerItem().apply {
          name = "??????????"
          featureType = TRAIT

        },
        FeatureContainerItem().apply {
          name = "????????????"
          featureType = Weapon.WEAPON

        },
        FeatureContainerItem().apply {
          name = "??????????"
          featureType = Armor.ARMOR
          onlyOne = true
        },
        FeatureContainerItem().apply {
          name = "??????????????????????"
          featureType = Skill.SKILL
        },
        FeatureContainerItem().apply {
          name = "??????????"
          featureType = Perk.PERK
        },
        FeatureContainerItem().apply {
          name = "????????"
          featureType = Power.POWER
          allowHidden = true
        }
    )

    val CREATURE_RACE_META = listOf(
        FeatureContainerItem().apply {
          name = "??????????"
          featureType = TRAIT

        }
    )
  }
}