package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.service.SizeProfile
import java.math.BigDecimal
import java.math.MathContext
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

@Entity
class WeaponAttack : DBObject(), FeatureContainerData {
  var mode: String = ""

  var damage: Int = 0
  var desturction: Int = 0
  var distance: BigDecimal = BigDecimal.ZERO
  var speed: Int = 0
  var clipSize: Int = 0
  var allowInBarrel: Boolean = false

  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "weapon_attack_id")
  override var features: MutableList<FeatureData> = mutableListOf()

  override fun meta(): List<FeatureContainerItem> = META

  fun display(): String = displayWithName(mode, features)

  fun display(prefix: String, additionalFeatures: List<FeatureData>): String = displayWithName("$prefix $mode".trim(), additionalFeatures + features)

  private fun displayWithName(name: String, features: List<FeatureData>) =
      (
          sequenceOf(
              name,
              "урон $damage/$desturction",
              "${distance.round(MathContext(2)).stripTrailingZeros().toPlainString()} м",
              "скр. $speed"
          ) +

              when {
                clipSize > 0 -> sequenceOf("Магазин ${clipSize()}")
                else -> emptySequence()
              } +

              features.asSequence().map { it.display() }
          )
          .filter { it.isNotBlank() }
          .joinToString()

  private fun clipSize() = when {
    allowInBarrel -> "$clipSize+1"
    else -> clipSize.toString()
  }

  fun adjust(sizeProfile: SizeProfile, externalFeatures: List<FeatureData>, isNatural: Boolean) = WeaponAttack().also {
    it.mode = mode

    val metalFist = externalFeatures.find { it.feature.name == "Железные кулаки" }?.let { it.x }?.toInt() ?: 0

    it.damage = damage + sizeProfile.damageModifier
    if (isNatural) {
      it.damage += metalFist
    }

    it.desturction = desturction + sizeProfile.destructionModifier
    it.distance = if (isNatural) sizeProfile.modifyNaturalWeaponDistance(distance)
    else sizeProfile.modifyWeaponDistance(distance)

    it.speed = speed
    it.clipSize = clipSize
    it.allowInBarrel = allowInBarrel
    it.features = features
  }

  companion object {
    val META = listOf(
        FeatureContainerItem().apply {
          name = "Свойства"
          featureType = "WEAPON_ATTACK_FEATURE"
        }
    )
  }
}