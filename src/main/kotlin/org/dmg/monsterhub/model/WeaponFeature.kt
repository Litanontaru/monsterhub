package org.dmg.monsterhub.model

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "old_weapon_feature")
class WeaponFeature : AbstractWeaponFeature() {
  @ManyToOne
  @JoinColumn(name = "weapon_id", nullable = true)
  lateinit var weapon: OldWeapon
}