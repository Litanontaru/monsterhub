package org.dmg.monsterhub.model.data

import javax.persistence.*

@Entity
class CreatureData() {
        @Id
        var name: String = ""

        @ElementCollection
        @CollectionTable(
                name = "creature_base",
                joinColumns = [JoinColumn(name = "creature_id")]
        )
        @Column(name = "base")
        var base: List<String> = mutableListOf()

        @ElementCollection
        @CollectionTable(
                name = "creature_trait",
                joinColumns = [JoinColumn(name = "creature_id")]
        )
        @Column(name = "base")
        var traits: List<String> = mutableListOf()
}