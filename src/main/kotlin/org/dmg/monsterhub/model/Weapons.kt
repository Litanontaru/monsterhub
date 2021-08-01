package org.dmg.monsterhub.model

data class Weapons(
        val name: String,
        val damage: Int,
        val destruction: Int,
        val distance: Double,
        val speed: Int,
        val features: List<WeaponFeature>
)

