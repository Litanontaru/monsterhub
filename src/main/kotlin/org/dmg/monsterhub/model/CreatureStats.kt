package org.dmg.monsterhub.model

data class CreatureStats(
    val name: String,
    val superiority: Superiority,
    val size: Int,
    val perception: List<String>,
    val movement: Map<String, Double>,
    val intelTraits: List<String>,
    val otherTraits: List<String>
)