package org.dmg.monsterhub.service

import java.math.BigDecimal

data class SizeProfile(
    val size: Int,
    val damageModifier: Int,
    val destructionModifier: Int,
    val partSizeModifier: BigDecimal,
    val weaponSizeModifier: BigDecimal,
    val speedModifier: BigDecimal,
    val powerUpModifier: Int
) {
  fun modifyWeaponDistance(distance: BigDecimal) =
      if (distance < ARMS) distance * partSizeModifier
      else ARMS * partSizeModifier + (distance - ARMS) * weaponSizeModifier

  fun modifyNaturalWeaponDistance(distance: BigDecimal) = distance * partSizeModifier

  companion object {
    private val ARMS = BigDecimal("0.6")

    operator fun invoke(row: List<String>) = SizeProfile(
        row[0].toInt(),
        row[1].toInt(),
        row[2].toInt(),
        row[3].toBigDecimal(),
        row[3].toBigDecimal(),
        row[4].toBigDecimal(),
        row[5].toInt()
    )
  }
}