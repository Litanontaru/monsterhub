package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.*
import org.springframework.stereotype.Service

@Service
object CreatureService {
  private val BASE = listOf(-1, 0, 0, 0, -26, 0, -13, 0, -18, 0, -3)

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
      if (index % 2 == 1) values.map { it[index] }.max() ?: 0
      else values.map { it[index] }.sum() + it.value
    }

    val off = evaluated[0] + evaluated[1]
    val def = evaluated[2] + evaluated[3]
    val per = evaluated[4] + evaluated[5]
    val hnd = evaluated[6] + evaluated[7]
    val mov = evaluated[8] + evaluated[9]
    val com = evaluated[10]

    val offence = off + (per + mov) / 2 + hnd
    val defence = def + (per + mov) / 2

    val sortedSuper = arrayOf(offence, defence, com).map { it.toDouble() }.sorted()

    val one = (sortedSuper[2] + sortedSuper[1]) / 2
    val another = (3 * sortedSuper[2] + sortedSuper[0]) / 4
    val sup = Math.max(one, another)

    val alpha = Math.max(offence, defence)
    val beta = Math.min(offence, defence)
    val cr = (2.0 * alpha + beta) / 3.0

    val aggregatedSup = getPowerSuperiority(creature)?.let { Math.max(it * 2.0, sup) } ?: sup

    return Superiority(
        Math.ceil(aggregatedSup / 2).toInt(),
        sup,
        sortedSuper[2] - sup,

        Math.ceil(cr / 2).toInt(),

        primary(offence, aggregatedSup),
        primary(defence, aggregatedSup),
        primary(com, aggregatedSup)
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

  fun physicalSize(creature: Creature): Int = partsSize(creature) +
      creature.getAllTraits("Крупногабаритный", "Крылатый").sumBy { 1 }

  fun partsSize(creature: Creature): Int =
      creature.getAllTraits("Тяжёлый")
          .singleOrNull()
          ?.let { size(creature) - 1 }
          ?: (creature.getAllTraits("Очень тяжёлый")
              .singleOrNull()
              ?.let { size(creature) - 3 }
              ?: size(creature))

  fun naturalWeapons(creature: Creature) =
      creature.getAllTraits("Естественное оружие")
          .map { it.feature.name to it.x.toInt() }.toList()
}