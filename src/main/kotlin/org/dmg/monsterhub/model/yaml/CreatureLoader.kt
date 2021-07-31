package org.dmg.monsterhub.model.yaml

import org.springframework.stereotype.Service
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

@Service
class CreatureLoader {
    operator fun invoke() =
            try {
                CreatureLoader::class.java.classLoader.getResourceAsStream("creatures.yml").use {
                    Yaml().loadAs(it, YCreatureList::class.java)
                            .creatures
                            .groupBy { c -> c.name }
                            .mapValues { e -> e.value.single() }

                }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

    operator fun invoke(yamlCreature: String) = Yaml().loadAs(yamlCreature, YCreature::class.java)
}