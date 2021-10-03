package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.service.SizeProfile
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

@Entity
class Weapon: Item() {
  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "weapon_id")
  var attacks: MutableList<WeaponAttack> = mutableListOf()

  override fun meta(): List<FeatureContainerItem> = META

  fun adjust(sizeProfile: SizeProfile, isNatural: Boolean, externalFeatures: List<FeatureData>) = Weapon().also {
    it.name = name
    it.attacks = attacks.asSequence().map { it.adjust(sizeProfile, externalFeatures, isNatural) }.toMutableList()
    it.features = features.asSequence().toMutableList()
    it.features = (features + externalFeatures).toMutableList()
  }

  companion object {
    val WEAPON = "WEAPON"

    val META = listOf(
        FeatureContainerItem().apply {
          name = "Свойства"
          featureType = "WEAPON_FEATURE"
        }
    )
  }
}