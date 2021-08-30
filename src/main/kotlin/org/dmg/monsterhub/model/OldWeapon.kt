package org.dmg.monsterhub.model

import javax.persistence.*

@Entity
@Table(name = "old_weapon")
class OldWeapon {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Long = 0

  var name: String = ""

  @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
  @JoinColumn(name = "weapon_id")
  var attacks: MutableList<OldWeaponAttack> = mutableListOf()

  @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
  @JoinColumn(name = "weapon_id")
  var features: MutableList<WeaponFeature> = mutableListOf()

  fun adjustToSize(sizeProfile: SizeProfile, isNatural: Boolean) = OldWeapon().also {
    it.name = name
    it.attacks = attacks.asSequence().map { it.adjustToSize(sizeProfile, isNatural) }.toMutableList()
    it.features = features.asSequence().toMutableList()
  }

  fun addExternalFeature(creatureTraits: List<CreatureTrait>): OldWeapon {
    val externalFeatures = creatureTraits
        .asSequence()
        .map {
          WeaponFeature().apply {
            feature = it.trait
            primaryNumber = it.x
            secondaryNumber = it.y
            details = it.details.lines().drop(1).joinToString("\n")
          }
        }

    return OldWeapon().also {
      it.name = name
      it.attacks = attacks.asSequence().toMutableList()
      it.features = (features.asSequence() + externalFeatures).toMutableList()
    }
  }
}