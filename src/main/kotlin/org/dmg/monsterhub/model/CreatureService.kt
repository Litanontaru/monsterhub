package org.dmg.monsterhub.model

import org.springframework.stereotype.Service
import java.lang.Math.max
import java.lang.Math.min

@Service
class CreatureService(
        val repository: CreatureRepository,
        val traitsService: TraitsService,
        val weaponService: WeaponService,
        val sizeProfileService: SizeProfileService
) {
    fun save(creature: Creature) {
        repository.save(creature)
    }

    fun find(name: String): Creature? = repository.findByName(name)

    private val base = listOf(0, 0, 0, 0, -26, 0, -13, 0, -18, 0, -3)

    fun superiority(creature: Creature): Superiority {
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

        val sortedSuper = arrayOf(offence, defence, com).sortedArray()
        val maxSuper = max(max(sortedSuper[0] * 4, sortedSuper[1] * 3), sortedSuper[2] * 2)

        val maxCR = max(min(offence, defence) * 4, max(offence, defence) * 3)

        return Superiority(
                Math.ceil(0.2 * maxSuper).toInt(),
                Math.ceil(0.2 * maxCR).toInt(),

                primary(offence, maxSuper),
                primary(defence, maxSuper),
                primary(com, maxSuper)
        )
    }

    private fun primary(value: Int, max: Int) = when {
        value * 4 < max -> PrimaryRate(value, (max / 4.0 - value).toInt())
        value * 3 < max -> PrimaryRate(value, (max / 3.0 - value).toInt())
        else -> PrimaryRate(value, (max / 2.0 - value).toInt())
    }

    fun size(creature: Creature) = creature
            .getAllTraits()
            .find { it.trait == "Размер" }
            ?.x
            ?: 0

    fun weapons(creature: Creature): List<Weapon> {
        val natural = creature.getAllTraits("Руки").map { "Кулаки" } +
                creature.getAllTraits("Естественное оружие").map { it.details }

        val sizeProfile = sizeProfileService[size(creature)]

        return weaponService
                .getNaturalWeapons(natural)
                .map { it.adjustToSize(sizeProfile) }
    }
}