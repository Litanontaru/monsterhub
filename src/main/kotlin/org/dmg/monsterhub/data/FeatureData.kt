package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.Feature
import javax.persistence.*

@Entity
class FeatureData : FeatureContainerData {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Long = 0

  @ManyToOne
  @JoinColumn(name = "feature_id", nullable = true)
  lateinit var feature: Feature

  var x: Int = 0
  var y: Int = 0
  var z: Int = 0

  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "feature_data_id")
  var designations: MutableList<FeatureDataDesignation> = mutableListOf()

  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "main_feature_id")
  override var features: MutableList<FeatureData> = mutableListOf()

  fun display(): String {
    return (sequenceOf(feature.name) +

        (if (x == 0) emptySequence() else sequenceOf(x)) +
        (if (y == 0) emptySequence() else sequenceOf(y)) +
        (if (z == 0) emptySequence() else sequenceOf(z)) +

        feature.designations.asSequence()
            .mapNotNull { key -> designations.find { it.designationKey == key } }
            .map { it.value } +

        feature.containFeatureTypes.asSequence()
            .flatMap { key -> features.asSequence().filter { it.feature.featureType == key.featureType } }
            .map { it.display() }

        )
        .joinToString()
  }
}