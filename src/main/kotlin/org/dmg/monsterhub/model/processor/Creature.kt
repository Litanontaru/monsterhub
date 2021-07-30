package org.dmg.monsterhub.model.processor

data class Creature(
        val name: String,
        val base: List<Creature>,
        val traits: List<CreatureTrait>
) {
    private val groups = traits.mapNotNull { it.trait.group }.distinct()

    fun allTraits(): List<CreatureTrait> = base.flatMap { it.allTraits().filterNot { it in groups } } + traits
}