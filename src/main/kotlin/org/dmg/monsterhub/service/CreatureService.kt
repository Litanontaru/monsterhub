package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.data.Power.Companion.POWER
import org.dmg.monsterhub.data.Superiority
import org.dmg.monsterhub.data.Trait
import org.springframework.stereotype.Service

@Service
object CreatureService {
  fun superiority(creature: Creature): Superiority {
    val allTraits = creature.getAllTraits()

    val values = allTraits
        .map { data ->
          data to (data.feature as Trait)
              .formulas(data.context)
              .map { it.calculateFinal().toInt() }
              .toList()
        }
        .toList()

    fun max(index: Int) =
      values
        .filter { it.second[index] > 0 }
        .maxBy { it.second[index] }
        ?.let {
          val rate = it.second[index]
          values.filter { it.second[index] == rate }
            .map { it.first.display() } to rate
        }
        ?: (listOf<String>() to 0)

    fun min(index: Int) =
      values
        .filter { it.second[index] < 0 }
        .minBy { it.second[index] }
        ?.let {
          val rate = it.second[index]
          values.filter { it.second[index] == rate }
            .map { it.first.display() } to rate
        }
        ?: (listOf<String>() to 0)

    fun alt(index: Int) = max(index) to min(index)

    val powers = getTraitPowers(creature) + getPowers(creature)
    val addedByPower = powers
      .flatMap {
        it.features.asSequence().filter { it.feature.name == "За происхождение" }.map { it.x.toInt() }
      }  //todo тут баг, потому что берётся только верхнеуровневые...
      .sum()

    val base = values.map { it.second[0] }.sum() + addedByPower - 149
    val off = alt(1)
    val def = alt(2)
    val com = alt(3)

    val traitRate = base + off.first.second + off.second.second + def.first.second + def.second.second + com.first.second + com.second.second
    val traitLevel = Math.ceil(traitRate / 6.0).toInt()
    val powerLevel = getPowerSuperiority(creature)

    val level = powerLevel?.let { Math.max(traitLevel, it) } ?: traitLevel

    return Superiority(
        level,
        level * 6 - traitRate,

        base,
        off.first,
        off.second,
        def.first,
        def.second,
        com.first,
        com.second
    )
  }

  private fun getPowerSuperiority(creature: Creature) = (getTraitPowers(creature) + getPowers(creature))
      .map { it.rate().toBigDecimal() }
      .max()
      ?.toInt()

  private fun getTraitPowers(creature: Creature) = creature
      .getAllTraits("Сила")
      .mapNotNull { it.features.singleOrNull() }
      .map { it.feature as Power }

  private fun getPowers(creature: Creature) = creature
      .getAll(POWER)
      .map { it.feature as Power }

  fun size(creature: Creature) = creature
      .getAllTraits("Размер")
      .singleOrNull()
      ?.x
      ?.toInt()
      ?: 0

  fun sizeProfile(creature: Creature) = SizeProfileService.get(
      size(creature),
      damageSize(creature),
      partsSize(creature),
      speedSize(creature)
  )

  fun damageSize(creature: Creature) = size(creature) +
      (creature.getAllTraits("Мощь").singleOrNull()?.x?.toInt() ?: 0)

  fun physicalSize(creature: Creature): Int = getBasePhysicalSize(creature) +
      creature.getAllTraits("Крупногабаритный", "Крылатый").sumBy { 1 }

  fun partsSize(creature: Creature): Int = getBasePhysicalSize(creature) +
      (creature.getAllTraits("Длинные конечности").singleOrNull()?.x?.toInt() ?: 0)

  fun speedSize(creature: Creature): Int = getBasePhysicalSize(creature)

  private fun getBasePhysicalSize(creature: Creature) = size(creature) +
      (creature.getAllTraits("Тяжёлый").singleOrNull()?.let { -1 } ?: 0) +
      (creature.getAllTraits("Очень тяжёлый").singleOrNull()?.let { -3 } ?: 0) +
      (creature.getAllTraits("Лёгкий").singleOrNull()?.let { 1 } ?: 0) +
      (creature.getAllTraits("Очень лёгкий").singleOrNull()?.let { 3 } ?: 0)

  fun naturalWeapons(creature: Creature) =
      creature.getAllTraits("Естественное оружие")
          .map { it.feature.name to it.x.toInt() }.toList()
}