package org.dmg.monsterhub.model.traits

data class Trait(
        val name: String,
        val rates: List<ValueRate>,
        val group: String?
) {
    companion object {
        operator fun invoke(row: List<String>): Trait {
            return Trait(
                    row[0],
                    row.drop(1).dropLast(1).map { ValueRate(it) },
                    row.last().takeIf { it.isNotEmpty() }
            )
        }
    }
}