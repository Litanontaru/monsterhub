package org.dmg.monsterhub.model

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class WeaponFeature: AbstractWeaponFeature() {
    @ManyToOne
    @JoinColumn(name="weapon_id", nullable=true)
    lateinit var weapon: Weapon
}