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
    fun save(creature: OldCreature) {
        repository.save(creature)
    }

    fun find(name: String): OldCreature? = repository.findByName(name)

    private val base = listOf(0, 0, 0, 0, -26, 0, -13, 0, -18, 0, -3)

    fun superiority(creature: OldCreature): Superiority {
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

    fun size(creature: OldCreature) = creature
            .getAllTraits("Размер")
            .singleOrNull()
            ?.x
            ?: 0

    private fun partsSize(creature: OldCreature): Int =
            creature.getAllTraits("Тяжёлый")
                    .singleOrNull()
                    ?.let { size(creature) - 1 }
                    ?: (creature.getAllTraits("Очень тяжёлый")
                            .singleOrNull()
                            ?.let { size(creature) - 3 }
                            ?: size(creature))

    private fun longArms(creature: OldCreature): Int =
            creature.getAllTraits("Длинные конечности")
                    .singleOrNull()
                    ?.x
                    ?: 0

    fun physicalSize(creature: OldCreature): Int = partsSize(creature) +
            creature.getAllTraits("Крупногабаритный", "Крылатый").sumBy { 1 }

    fun weapons(creature: OldCreature): List<Weapon> {
        val natural = creature.getAllTraits("Руки").map { "Кулаки" } +
                creature.getAllTraits("Естественное оружие").map { it.details } +
                creature.getAllTraits("Оружие").map { it.trait }

        val sizeProfile = sizeProfileService.get(
                size(creature),
                partsSize(creature) + longArms(creature)
        )

        val attackTraits = creature.getAllTraits("Свойство атаки").groupBy { getWeaponFromAttackTrait(it) }

        return weaponService
                .getNaturalWeapons(natural)
                .map { it.adjustToSize(sizeProfile, weaponService.isNatural(it)) }
                .map { weapon -> attackTraits[weapon.name]?.let { weapon.addExternalFeature(it) } ?: weapon }
    }

    private fun getWeaponFromAttackTrait(creatureTrait: CreatureTrait) = creatureTrait
            .details
            .takeIf { it.isNotBlank() }
            ?.let { it.lines()[0] }
}