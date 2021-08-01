package org.dmg.monsterhub.model

import org.hibernate.annotations.Type
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class CreatureTrait: Detailed {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    var trait: String = ""

    @Type(type="text")
    override var details: String = ""

    var x: Int = 0

    var y : Int = 0

    var traitGroup: String? = null

    var traitCategory: String = ""

    fun toSmallString() = "$trait${if (x == 0) "" else " $x"}${if (y == 0) "" else " $y"}"
}