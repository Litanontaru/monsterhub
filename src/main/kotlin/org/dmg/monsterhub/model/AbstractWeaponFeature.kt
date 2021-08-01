package org.dmg.monsterhub.model

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class AbstractWeaponFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    var feature: String = ""
    var primary: Int = 0
    var secondary: Int = 0
    var details: String = ""
}