package org.dmg.monsterhub.data

import javax.persistence.*

@Entity
class FeatureData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name="feature_id", nullable=true)
    lateinit var feature: FeatureData

    var x: Int = 0
    var y: Int = 0
    var z: Int = 0

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "feature_data_id")
    var designations: MutableList<FeatureDataDesignation> = mutableListOf()
}