package org.dmg.monsterhub.data

data class Superiority(
    val value: Int,
    val underRate: Int,

    val base: Int,
    val offMax: Pair<List<String>, Int>,
    val offMin: Pair<List<String>, Int>,
    val defMax: Pair<List<String>, Int>,
    val defMin: Pair<List<String>, Int>,
    val comMax: Pair<List<String>, Int>,
    val comMin: Pair<List<String>, Int>
)
