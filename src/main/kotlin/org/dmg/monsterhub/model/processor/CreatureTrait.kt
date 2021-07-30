package org.dmg.monsterhub.model.processor

import org.dmg.monsterhub.model.traits.Trait


data class CreatureTrait(
        val trait: Trait,
        val x: Int,
        val y: Int
) {
    val value = trait.rates.map { it.evaluate(x, y) }
}

operator fun List<String>.contains(trait: CreatureTrait) = trait.trait.group ?. let { it in this } ?: false