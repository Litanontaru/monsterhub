package org.dmg.monsterhub.data

data class Superiority(
    val value: Int,
    val level: Double,
    val skew: Double,

    val challengeRating: Int,

    val offence: PrimaryRate,
    val defence: PrimaryRate,
    val common: PrimaryRate
)

data class PrimaryRate(val value: Int, val under: Int)

