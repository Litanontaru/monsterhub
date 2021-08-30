package org.dmg.monsterhub.data

import javax.persistence.*

@Entity
class WeaponAttack : FeatureContainerData {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Long = 0

  var mode: String = ""

  var damage: Int = 0
  var desturction: Int = 0
  var distance: Double = 0.0
  var speed: Int = 0
  var clipSize: Int = 0
  var allowInBarrel: Boolean = false

  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "weapon_attack_id")
  override var features: MutableList<FeatureData> = mutableListOf()

  @Transient
  var deleteOnly: Boolean = false

  fun display(): String {
    return (
          sequenceOf(
              mode,
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
        .joinToString()
  }

  private fun clipSize() = when {
    allowInBarrel -> "$clipSize+1"
    else -> clipSize.toString()
  }
}