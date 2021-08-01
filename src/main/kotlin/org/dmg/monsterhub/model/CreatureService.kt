package org.dmg.monsterhub.model

import org.dmg.monsterhub.model.traits.TraitsService
import org.springframework.stereotype.Service

@Service
class CreatureService(
        val repository: CreatureRepository,
        val traitsService: TraitsService
) {
    fun save(creature: Creature) {
        repository.save(creature)
    }

    fun find(name: String): Creature? = repository.findByName(name)

    private val base = listOf(0, 0, 0, 0, -26, 0, -13, 0, -18, 0, -3)

    fun eval(creature: Creature): Superiority {
        val allTraits = creature.getAllTraits()

        val values = allTraits
                .map {
                    traitsService[it.trait]!!.let { trait ->
                        trait.rates.map { rate ->
                            rate.evaluate(it.x, it.y)
                        }
                    }
                }
                .toList()


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

        val offence = off + (per + mov) / 2 + hnd
        val defence = def + (per + mov) / 2

        return sup(offence, defence, com)
    }

    private fun sup(offence: Int, defence: Int, common: Int): Superiority {
        val sorted = arrayOf(offence, defence, common).sortedArray()
        val max = Math.max(Math.max(sorted[0] * 4, sorted[1] * 3), sorted[2] * 2)
        return Superiority(
                Math.ceil(0.2 * max).toInt(),
                offence,
                defence,
                common
        )
    }
}