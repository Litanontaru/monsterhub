package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.*
import org.springframework.stereotype.Service

@Service
object CreatureService {
  private val base = listOf(-1, 0, 0, 0, -26, 0, -13, 0, -18, 0, -3)

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


    val evaluated = base.withIndex().map {
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

    val sortedSuper = arrayOf(offence, defence, com).sortedArray()
    val maxSuper = Math.max(Math.max(sortedSuper[0] * 4, sortedSuper[1] * 3), sortedSuper[2] * 2)

    val maxCR = Math.max(Math.min(offence, defence) * 4, Math.max(offence, defence) * 3)

    return Superiority(
        Math.ceil(0.2 * maxSuper).toInt(),
        Math.ceil(0.2 * maxCR).toInt(),

        primary(offence, maxSuper),
        primary(defence, maxSuper),
        primary(com, maxSuper)
    )
  }

  private fun primary(value: Int, max: Int) = when {
    value * 4 < max -> PrimaryRate(value, (max / 4.0 - value).toInt())
    value * 3 < max -> PrimaryRate(value, (max / 3.0 - value).toInt())
    else -> PrimaryRate(value, (max / 2.0 - value).toInt())
  }

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

  fun naturalWeapons(creature: Creature): List<String> =
      creature.getAllTraits("Естественное оружие").map { it.feature.name }.toList()
}