package org.dmg.monsterhub.model.processor

import org.dmg.monsterhub.model.traits.TraitsService
import org.dmg.monsterhub.model.yaml.CreatureLoader
import org.dmg.monsterhub.model.yaml.YCreature
import org.springframework.stereotype.Service
import java.lang.Integer.parseInt

@Service
class CreatureServiceOld(
        val loader: CreatureLoader,
        val traits: TraitsService
) {


    private final val yCretures = loader();
    private val creatures = mutableMapOf<String, Creature>();

    init {
        yCretures.keys.forEach { creature(it) }
    }

    fun trait(row: String): CreatureTrait {
        val split = row.split(",".toRegex()).map { it.trim() }
        return CreatureTrait(
                traits[split[0].replace("\\((.)*\\)".toRegex(), "").trim()]!!,
                if (split.size > 1) parseInt(split[1].trim()) else 0,
                if (split.size > 2) parseInt(split[2].trim()) else 0
        )
    }

    fun creature(name: String): Creature = creatures.computeIfAbsent(name) {
        yCretures[it]?.creature() ?: throw RuntimeException("Unknown creature $this")
    }

    private fun YCreature.creature() = Creature(
            this.name,
            this.base.map { base -> creature(base) },
            this.traits.map { trait(it) }
    )

    private val base = listOf(0, 0, 0, 0, -26, 0, -13, 0, -18, 0, -3)

    fun eval(yamlCreature: String): Int {
        val values: List<List<Int>> = loader(yamlCreature)
                .creature()
                .allTraits()
                .map { it.value }

        val evaluated = base.withIndex().map {
            val index = it.index
            if (index % 2 == 1) values.map { it[index] }.max() ?: 0
            else values.map { it[index] }.sum() + it.value
        }

        val off = evaluated[0] + evaluated[1]
        val def = evaluated[2] + evaluated[3]
        val per = evaluated[4] + evaluated[5]
        val hnd = evaluated[6] + evaluated[7]
        val mov = evaluated[8] + evaluated[9]
        val com = evaluated[10]

        val offence = off + per + hnd + mov / 2
        val defence = def + (per + mov) / 2

        return sup(offence, defence, com)
    }

    private fun sup(offence: Int, defence: Int, com: Int): Int {
        val sorted = arrayOf(offence, defence, com).sortedArray()
        val max = Math.max(Math.max(sorted[0] * 4, sorted[1] * 3), sorted[2] * 2)
        return Math.ceil(0.2 * max).toInt()
    }
}