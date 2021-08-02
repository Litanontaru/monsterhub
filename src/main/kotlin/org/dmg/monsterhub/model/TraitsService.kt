package org.dmg.monsterhub.model

import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Service
class TraitsService {
    private val traits = csv("/Traits.csv", 13)
            .asSequence()
            .map { Trait(it) }
            .groupBy { it.name }
            .mapValues { it.value.single() }

    operator fun get(trait: String): Trait? = traits[trait]
}

fun csv(fileName: String, columns: Int): List<List<String>> = try {
    BufferedReader(InputStreamReader(TraitsService::class.java.getResourceAsStream(fileName)))
            .use { br ->
                br.lineSequence()
                        .map { it.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
                        .map { (it.asSequence() + (it.size..columns).map { "" }).toList() }
                        .toList()
            }
} catch (e: IOException) {
    emptyList()
}