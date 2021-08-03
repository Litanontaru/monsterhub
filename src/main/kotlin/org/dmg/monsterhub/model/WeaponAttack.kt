package org.dmg.monsterhub.model

import javax.persistence.*

@Entity
class WeaponAttack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    var mode: String = ""

    var damage: Int = 0
    var desturction: Int = 0
    var distance: Double = 0.0
    var speed: Int = 0
    var clipsize: Int = 0
    var allowInBarrel: Boolean = false

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "weapon_attack_id")
    var features: MutableList<WeaponAttackFeature> = mutableListOf()

    fun adjustToSize(sizeProfile: SizeProfile) = WeaponAttack().also {
        it.mode = mode

        it.damage = damage + sizeProfile.damageModifier
        it.desturction = desturction + sizeProfile.destructionModifier
        it.distance = sizeProfile.modifyWeaponDistance(distance)
        it.speed = speed
        it.clipsize = clipsize
        it.allowInBarrel = allowInBarrel
        it.features = features
    }
}