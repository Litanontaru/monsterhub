package org.dmg.monsterhub.data

import org.hibernate.annotations.Type
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class FeatureDataDesignation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    var designationKey: String = ""

    @Type(type="text")
    var value: String = ""
}