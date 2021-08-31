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