package org.dmg.monsterhub.data.meta

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class FeatureContainerItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    lateinit var featureType: String

    var name: String = ""

    var onlyOne: Boolean = false
}