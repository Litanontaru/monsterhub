package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.*
import org.dmg.monsterhub.data.Power.Companion.POWER
import org.springframework.stereotype.Service

@Service
object CreatureService {
  private val BASE = listOf(-43, 0, -41, 0, -55, 0)

  fun superiority(creature: Creature): Superiority {
    val allTraits = creature.getAllTraits()

    val values = allTraits
        .map { data ->
          (data.feature as Trait)
              .formulas(data.context)
              .map { it.calculateFinal().toInt() }
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

    val powers = getTraitPowers(creature) + getPowers(creature)

    val addedByPower = powers
        .map { it.skillType to it.features.filter { it.feature.name == "За происхождение" }.map { it.x } }
        .filter { it.second.isNotEmpty() }
        .groupBy { it.first }
        .mapValues { it.value.flatMap { it.second }.map { it.toInt() }.sum() }

    val offence = evaluated[0] + evaluated[1] + addedByPower.getOrDefault(SkillType.OFFENSE, 0)
    val defence = evaluated[2] + evaluated[3] + addedByPower.getOrDefault(SkillType.DEFENCE, 0)
    val common = evaluated[4] + evaluated[5] + addedByPower.getOrDefault(SkillType.COMMON, 0)

    val sortedSuper = arrayOf(offence, defence, common).map { it.toDouble() }.sorted()

    val one = (sortedSuper[2] + sortedSuper[1]) / 2
    val another = (2 * sortedSuper[2] + sortedSuper[0]) / 3
    val sup = Math.max(one, another)

    val cr = (offence + defence) / 2.0

    val aggregatedSup = getPowerSuperiority(creature)?.let { Math.max(it * 2.0, sup) } ?: sup

    val level = Math.ceil(aggregatedSup / 2).toInt()
    val skew = (sortedSuper[2] - sup).toInt()
    val expected = arrayOf(2 * level + skew, 2 * level - skew, 2 * level - 2 * skew)

    return Superiority(
        level,
        sup,
        sortedSuper[2] - sup,

        Math.ceil(cr / 2).toInt(),

        primary(offence, expected),
        primary(defence, expected),
        primary(common, expected)
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

  private fun primary(actual: Int, expected: Array<Int>) = PrimaryRate(actual, expected.map { it - actual }.filter { it > 0 }.min()
      ?: 0)

  fun size(creature: Creature) = creature
      .getAllTraits("Размер")
      .singleOrNull()
      ?.x
      ?.toInt()
      ?: 0

  fun sizeProfile(creature: Creature) = SizeProfileService.get(size(creature), damageSize(creature), partsSize(creature))

  fun damageSize(creature: Creature) = size(creature) +
      (creature.getAllTraits("Мощь").singleOrNull()?.x?.toInt() ?: 0)

  fun physicalSize(creature: Creature): Int = getBasePhysicalSize(creature) +
      creature.getAllTraits("Крупногабаритный", "Крылатый").sumBy { 1 }

  fun partsSize(creature: Creature): Int = getBasePhysicalSize(creature) +
      (creature.getAllTraits("Длинные конечности").singleOrNull()?.x?.toInt() ?: 0)

  private fun getBasePhysicalSize(creature: Creature) = size(creature) +
      (creature.getAllTraits("Тяжёлый").singleOrNull()?.let { -1 } ?: 0) +
      (creature.getAllTraits("Очень тяжёлый").singleOrNull()?.let { -3 } ?: 0) +
      (creature.getAllTraits("Лёгкий").singleOrNull()?.let { 1 } ?: 0) +
      (creature.getAllTraits("Очень лёгкий").singleOrNull()?.let { 3 } ?: 0)

  fun naturalWeapons(creature: Creature) =
      creature.getAllTraits("Естественное оружие")
          .map { it.feature.name to it.x.toInt() }.toList()
}