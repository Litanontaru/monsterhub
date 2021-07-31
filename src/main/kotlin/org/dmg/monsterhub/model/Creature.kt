package org.dmg.monsterhub.model

import javax.persistence.*

@Entity
class Creature {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    var name: String = ""

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
            name = "Base_Creature",
            joinColumns = [JoinColumn(name = "creature_id")],
            inverseJoinColumns = [JoinColumn(name = "base_id")]
    )
    var base: MutableList<Creature> = mutableListOf()


}