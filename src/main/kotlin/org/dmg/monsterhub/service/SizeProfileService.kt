package org.dmg.monsterhub.service

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object SizeProfileService {
  val sizeProfiles = csv("/size.csv", 2)
      .asSequence()
      .map { SizeProfile(it) }
      .groupBy { it.size }
      .mapValues { it.value.single() }

  fun get(size: Int, damageSize: Int, partsSize: Int, speedSize: Int = 0): SizeProfile {
    val bySize = sizeProfiles[size]!!
    val byDamageSize = sizeProfiles[damageSize]!!

    return SizeProfile(
        size = bySize.size,
        damageModifier = byDamageSize.damageModifier,
        destructionModifier = byDamageSize.destructionModifier,
        damageReduction = bySize.damageReduction,
        destructionReduction = bySize.destructionReduction,
        partSizeModifier = sizeProfiles[partsSize]!!.partSizeModifier,
        weaponSizeModifier = bySize.weaponSizeModifier,
        speedModifier = sizeProfiles[speedSize]!!.speedModifier,
        powerUpModifier = bySize.powerUpModifier
    )
  }
}

fun csv(fileName: String, columns: Int): List<List<String>> = try {
  BufferedReader(InputStreamReader(SizeProfileService::class.java.getResourceAsStream(fileName)))
      .use { br ->
        br.lineSequence()
            .map { it.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
            .map { (it.asSequence() + (it.size..columns).map { "" }).toList() }
            .toList()
      }
} catch (e: IOException) {
  emptyList()
}