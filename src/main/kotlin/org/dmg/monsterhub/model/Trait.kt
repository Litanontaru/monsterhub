package org.dmg.monsterhub.model

data class Trait(
        val name: String,
        val rates: List<ValueRate>,
        val group: String?,
        val category: String
) {
    companion object {
        operator fun invoke(row: List<String>): Trait {
            return Trait(
                    row[0],
                    row.drop(1).dropLast(2).map { ValueRate(it) },
                    row.get(row.lastIndex - 1).takeIf { it.isNotEmpty() },
                    row.last()
            )
        }
    }
}