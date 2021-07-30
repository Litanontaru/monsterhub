package org.dmg.monsterhub.model.traits

import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Service
class TraitsService {
    private val traits = csv("/Traits.csv")
            .asSequence()
            .map { Trait(it) }
            .groupBy { it.name }
            .mapValues { it.value.single() }

    private fun csv(fileName: String): List<List<String>> = try {
        BufferedReader(InputStreamReader(TraitsService::class.java.getResourceAsStream(fileName)))
                .use { br ->
                    br.lineSequence()
                            .map { it.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
                            .map { (it.asSequence() + (it.size..13).map { "" }).toList() }
                            .toList()
                }
    } catch (e: IOException) {
        emptyList()
    }

    operator fun get(trait: String): Trait? = traits[trait]
}