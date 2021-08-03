package org.dmg.monsterhub.model

import org.springframework.stereotype.Service

@Service
class SizeProfileService {
    val sizeProfiles = csv("/size.csv", 2)
            .asSequence()
            .map { SizeProfile(it) }
            .groupBy { it.size }
            .mapValues { it.value.single() }

    fun get(size: Int, partsSize: Int): SizeProfile {
        val bySize = sizeProfiles[size]!!
        val byParts = sizeProfiles[partsSize]!!

        return SizeProfile(
                bySize.size,
                bySize.damageModifier,
                bySize.destructionModifier,
                byParts.partSizeModifier,
                bySize.weaponSizeModifier,
                byParts.speedModifier
        )
    }
}