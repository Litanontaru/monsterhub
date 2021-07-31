package org.dmg.monsterhub.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class CreatureTrait {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    var trait: String = ""

    var details: String = ""

    var x: Int = 0

    var y : Int = 0
}