package org.dmg.monsterhub.data

import org.dmg.monsterhub.service.SizeProfile
import java.math.BigDecimal
import javax.persistence.*

@Entity
class WeaponAttack : FeatureContainerData {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Long = 0

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

  @Transient
  var deleteOnly: Boolean = false

  fun display(): String = displayWithName(mode, features)

  fun display(prefix: String, additionalFeatures: List<FeatureData>): String = displayWithName("$prefix $mode".trim(), additionalFeatures + features)

  private fun displayWithName(name: String, features: List<FeatureData>) =
      (
          sequenceOf(
              name,
              "урон $damage/$desturction",
              "$distance м",
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

  fun adjustToSize(sizeProfile: SizeProfile, isNatural: Boolean) = WeaponAttack().also {
    it.mode = mode

    it.damage = damage + sizeProfile.damageModifier
    it.desturction = desturction + sizeProfile.destructionModifier
    it.distance = if (isNatural) sizeProfile.modifyNaturalWeaponDistance(distance)
    else sizeProfile.modifyWeaponDistance(distance)

    it.speed = speed
    it.clipSize = clipSize
    it.allowInBarrel = allowInBarrel
    it.features = features
  }
}