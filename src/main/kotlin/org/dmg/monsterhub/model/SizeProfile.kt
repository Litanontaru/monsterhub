package org.dmg.monsterhub.model

data class SizeProfile(
    val size: Int,
    val damageModifier: Int,
    val destructionModifier: Int,
    val partSizeModifier: Double,
    val weaponSizeModifier: Double,
    val speedModifier: Double
) {
  fun modifyWeaponDistance(distance: Double) =
      if (distance < 0.6) distance * partSizeModifier
      else 0.6 * partSizeModifier + (distance - 0.6) * weaponSizeModifier

  fun modifyNaturalWeaponDistance(distance: Double) = distance * partSizeModifier

  companion object {
    operator fun invoke(row: List<String>) = SizeProfile(
        row[0].toInt(),
        row[1].toInt(),
        row[2].toInt(),
        row[3].toDouble(),
        row[3].toDouble(),
        row[4].toDouble()
    )
  }
}