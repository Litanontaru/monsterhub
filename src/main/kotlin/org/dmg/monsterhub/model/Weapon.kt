package org.dmg.monsterhub.model

import javax.persistence.*

@Entity
class Weapon {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    var name: String = ""

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "weapon_id")
    var attacks: MutableList<WeaponAttack> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "weapon_id")
    var features: MutableList<WeaponFeature> = mutableListOf()
}