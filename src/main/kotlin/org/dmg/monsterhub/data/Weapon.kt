package org.dmg.monsterhub.data

import org.dmg.monsterhub.service.SizeProfile
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

@Entity
class Weapon: Item() {
  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "weapon_id")
  var attacks: MutableList<WeaponAttack> = mutableListOf()

  fun adjust(sizeProfile: SizeProfile, isNatural: Boolean, externalFeatures: List<FeatureData>) = Weapon().also {
    it.name = name
    it.attacks = attacks.asSequence().map { it.adjustToSize(sizeProfile, isNatural) }.toMutableList()
    it.features = features.asSequence().toMutableList()
    it.features = (features + externalFeatures).toMutableList()
  }
}