package org.dmg.monsterhub.model.yaml

import org.springframework.stereotype.Service
import org.yaml.snakeyaml.Yaml

@Service
class CreatureLoader {
    private var yCreatures: Map<String, YCreature> = emptyMap()

    init {
        try {
            CreatureLoader::class.java.classLoader.getResourceAsStream("creatures.yml").use {
                yCreatures = Yaml().loadAs(it, YCreatureList::class.java)
                        .creatures
                        .groupBy { c -> c.name }
                        .mapValues { e -> e.value.single() }

            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}