package org.dmg.monsterhub.data

data class Superiority(
    val value: Int,
    val challengeRating: Int,

    val offence: PrimaryRate,
    val defence: PrimaryRate,
    val common: PrimaryRate
)

data class PrimaryRate(val value: Int, val underDate: Int)

