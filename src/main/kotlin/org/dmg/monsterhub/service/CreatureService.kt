package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.*
import org.dmg.monsterhub.data.Armor.Companion.ARMOR
import org.dmg.monsterhub.service.Damage.Companion.ZERO_DAMAGE
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

    val maxCR = Math.max(Math.min(offence, defence) * 3, Math.max(offence, defence) * 2)

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

  fun defenceProfile(creature: Creature): DefenceProfile {
    val size = sizeProfile(creature)
    val armor = creature.features.find { it.feature.featureType == ARMOR }?.let { it.feature as Armor }
    val groupSize = creature.getAllTraits("Группа").take(1).singleOrNull()?.x?.toInt() ?: 0

    val naturalArmor = creature.getAllTraits("Естественная защита").take(1).singleOrNull()?.x?.toInt() ?: 0

    return DefenceProfile(
        damageReduction = size.damageModifier,
        destructionReduction = size.destructionModifier + groupSize * size.powerUpModifier,

        strongArmor = naturalArmor + (armor?.strong ?: 0),
        standardArmor = naturalArmor + (armor?.standard ?: 0),
        weakArmor = naturalArmor + (armor?.weak ?: 0),

        thick = armor?.features?.find { it.feature.name == "Плотная" }?.let { it.x.toInt() * size.powerUpModifier }
            ?: 0,
        stormArmorModifier = armor?.features?.find { it.feature.name == "Штуровая" }?.let { size.powerUpModifier } ?: 0,
        isVest = armor?.features?.find { it.feature.name == "Жилет" }?.let { true } ?: false
    )
  }
}

enum class AttackEffectiveness(val display: String) {
  LIMITED("Ограниченное"),
  STANDARD("Стандартное"),
  PERFECT("Превосходное"),
  EPIC("Эпическое");

  override fun toString(): String = display
}

enum class WeaponType(val display: String) {
  DISTANT("Стрелковое"),
  THRUST("Колющее"),
  OTHER("Иное");

  override fun toString(): String = display
}

data class DefenceProfile(
    val damageReduction: Int,
    val destructionReduction: Int,

    val strongArmor: Int,
    val standardArmor: Int,
    val weakArmor: Int,

    val thick: Int,
    val stormArmorModifier: Int,
    val isVest: Boolean
) {
  fun damage(
      damage: Int,
      destruction: Int,
      effectiveness: AttackEffectiveness,
      weaponType: WeaponType
  ): Damage {
    val rDamage = damage - damageReduction
    if (rDamage <= 0) {
      return ZERO_DAMAGE
    }
    val rDestruction = destruction - destructionReduction
    if (rDamage + rDestruction <= 0) {
      return ZERO_DAMAGE
    }

    return when (effectiveness) {
      AttackEffectiveness.LIMITED -> min(
          standard(rDamage, rDestruction, strongArmor(weaponType)),
          safe(rDamage, rDestruction, standardArmor(weaponType))
      )
      AttackEffectiveness.STANDARD -> standard(rDamage, rDestruction, standardArmor(weaponType))
      AttackEffectiveness.PERFECT -> max(
          standard(rDamage, rDestruction, weakArmor(weaponType)),
          fatal(rDamage, rDestruction, standardArmor(weaponType))
      )
      AttackEffectiveness.EPIC -> fatal(rDamage, rDestruction, weakArmor(weaponType))
    }
  }

  private fun max(a: Damage, b: Damage) = if (a > b) a else b

  private fun min(a: Damage, b: Damage) = if (a < b) a else b

  private fun strongArmor(weaponType: WeaponType): Int = when (weaponType) {
    WeaponType.OTHER -> strongArmor
    else -> strongArmor + stormArmorModifier
  }

  private fun standardArmor(weaponType: WeaponType): Int = when (weaponType) {
    WeaponType.OTHER -> standardArmor
    else -> standardArmor + stormArmorModifier
  }

  private fun weakArmor(weaponType: WeaponType): Int = when (weaponType) {
    WeaponType.DISTANT -> weakArmor
    else -> if (isVest) 0 else weakArmor
  }

  private fun standard(damage: Int, destruction: Int, armor: Int): Damage {
    if (damage <= armor) {
      return when {
        damage * (destruction - thick) <= armor -> ZERO_DAMAGE
        else -> Damage(destruction, 0)
      }
    }
    val rDamage = damage - armor
    return when {
      rDamage <= 3 -> Damage(rDamage + destruction, 0)
      rDamage <= 5 -> Damage(3 + destruction, rDamage - 3 + destruction)
      else -> Damage(3 + destruction, 2 + destruction)
    }
  }

  private fun safe(damage: Int, destruction: Int, armor: Int): Damage {
    if (damage <= armor) {
      return when {
        damage * (destruction - thick) <= armor -> ZERO_DAMAGE
        else -> Damage(destruction, 0)
      }
    }
    val rDamage = damage - armor
    return when {
      rDamage <= 3 -> Damage(rDamage + destruction, 0)
      else -> Damage(3 + destruction, 0)
    }
  }

  private fun fatal(damage: Int, destruction: Int, armor: Int): Damage {
    if (damage <= armor) {
      return when {
        damage * (destruction - thick) <= armor -> ZERO_DAMAGE
        else -> Damage(0, destruction)
      }
    }
    val rDamage = damage - armor
    return when {
      rDamage <= 4 -> Damage(0, rDamage + destruction)
      else -> Damage(0, 4 + destruction)
    }
  }
}

data class Damage(
    val flesh: Int,
    val vitals: Int
) {
  operator fun compareTo(other: Damage): Int = COMPARATOR.compare(this, other)

  private fun stun() = if (flesh > 1) "Оглушение" else null
  private fun wear() = if (flesh > 3) "Износ ${flesh - 3}" else null
  private fun trauma() = if (vitals > 0) "Травма ${(vitals + 2) / 2}" else null

  fun display() = listOfNotNull(stun(), wear(), trauma()).joinToString()

  companion object {
    val ZERO_DAMAGE = Damage(0, 0)
    val COMPARATOR = Comparator.comparingInt<Damage> { it.vitals }.thenComparingInt { it.flesh }!!
  }
}