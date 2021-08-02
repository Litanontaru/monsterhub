package org.dmg.monsterhub.model

import org.springframework.stereotype.Service

@Service
class SizeProfileService {
    val sizeProfiles = csv("/size.csv", 2)
            .asSequence()
            .map { SizeProfile(it) }
            .groupBy { it.size }
            .mapValues { it.value.single() }

    operator fun get(size: Int): SizeProfile = sizeProfiles[size]!!
}