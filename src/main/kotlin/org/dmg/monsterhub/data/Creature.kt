package org.dmg.monsterhub.data

import javax.persistence.*

@Entity
class Creature: FeatureContainerData() {
    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
            name = "Base_Creature",
            joinColumns = [JoinColumn(name = "creature_id")],
            inverseJoinColumns = [JoinColumn(name = "base_id")]
    )
    var base: MutableList<Creature> = mutableListOf()
}