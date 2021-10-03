package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.*
import org.springframework.stereotype.Service

@Service
object CreatureService {
  private val BASE = listOf(-18, 0, -14, 0, -18, 0)

  fun superiority(creature: Creature): Superiority {
    val allTraits = creature.getAllTraits()

    val values = allTraits
        .map { data ->
          (data.feature as Trait)
              .formulas(data.context)
              .map { it.calculate().toInt() }
              .toList()
        }
        .toList()


    val evaluated = BASE.withIndex().map {
      val index = it.index
      when {
        index % 2 == 1 ->
          values.partition { it[index] > 0 }
              .let { (positive, negative) ->
                (positive.map { it[index] }.max() ?: 0) + (negative.map { it[index] }.min() ?: 0)
              }
        else -> values.map { it[index] }.sum() + it.value
      }
    }

    val offence = evaluated[0] + evaluated[1]
    val defence = evaluated[2] + evaluated[3]
    val common = evaluated[4] + evaluated[5]

    val sortedSuper = arrayOf(offence, defence, common).map { it.toDouble() }.sorted()

    val one = (sortedSuper[2] + sortedSuper[1]) / 2
    val another = (2 * sortedSuper[2] + sortedSuper[0]) / 3
    val sup = Math.max(one, another)

    val cr = (offence + defence) / 2.0

    val aggregatedSup = getPowerSuperiority(creature)?.let { Math.max(it * 2.0, sup) } ?: sup

    return Superiority(
        Math.ceil(aggregatedSup / 2).toInt(),
        sup,
        sortedSuper[2] - sup,

        Math.ceil(cr / 2).toInt(),

        primary(offence, aggregatedSup),
        primary(defence, aggregatedSup),
        primary(common, aggregatedSup)
    )
  }

  private fun getPowerSuperiority(creature: Creature) = creature
      .getAllTraits("Сила")
      .mapNotNull { it.features.singleOrNull() }
      .map { it.feature as Power }
      .map { it.rate().toBigDecimal() }
      .max()
      ?.toInt()

  private fun primary(value: Int, sup: Double) = PrimaryRate(value, sup - value)

  fun size(creature: Creature) = creature
      .getAllTraits("Размер")
      .singleOrNull()
      ?.x
      ?.toInt()
      ?: 0

  fun sizeProfile(creature: Creature) = SizeProfileService.get(size(creature), partsSize(creature))

  fun physicalSize(creature: Creature): Int = getBasePhysicalSize(creature) +
      creature.getAllTraits("Крупногабаритный", "Крылатый").sumBy { 1 }

  fun partsSize(creature: Creature): Int = getBasePhysicalSize(creature) +
      (creature.getAllTraits("Длинные конечности").singleOrNull()?.x?.toInt() ?: 0)

  private fun getBasePhysicalSize(creature: Creature) = size(creature) +
      (creature.getAllTraits("Тяжёлый").singleOrNull()?.let { -1 } ?: 0) +
      (creature.getAllTraits("Очень тяжёлый").singleOrNull()?.let { -3 } ?: 0)

  fun naturalWeapons(creature: Creature) =
      creature.getAllTraits("Естественное оружие")
          .map { it.feature.name to it.x.toInt() }.toList()
}