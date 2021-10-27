package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Armor
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.service.Damage.Companion.ZERO_DAMAGE

object DamageService {
  fun defenceProfile(creature: Creature): DefenceProfile {
    val size = CreatureService.sizeProfile(creature)
    val armor = creature.features.find { it.feature.featureType == Armor.ARMOR }?.let { it.feature as Armor }
    val groupSize = creature.getAllTraits("Группа").take(1).singleOrNull()?.x?.toInt() ?: 0

    val naturalArmor = creature.getAllTraits("Естественная защита").take(1).singleOrNull()?.x?.toInt() ?: 0

    return DefenceProfile(
        damageReduction = size.damageReduction,
        destructionReduction = size.destructionReduction + groupSize * size.powerUpModifier,

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