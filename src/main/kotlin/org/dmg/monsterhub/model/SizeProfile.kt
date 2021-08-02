package org.dmg.monsterhub.model

data class SizeProfile(
        val size: Int,
        val damageModifier: Int,
        val destructionModifier: Int
) {
    companion object {
        operator fun invoke(row: List<String>) = SizeProfile(
            Integer.parseInt(row[0]),
            Integer.parseInt(row[1]),
            Integer.parseInt(row[2])
        )
    }
}