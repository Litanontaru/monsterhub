package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.service.Decimal
import org.dmg.monsterhub.service.Formula.toFormula
import java.math.BigDecimal
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
  var xa: Int = 0
  var y: Int = 0
  var ya: Int = 0
  var z: Int = 0
  var za: Int = 0

  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "feature_data_id")
  var designations: MutableList<FeatureDataDesignation> = mutableListOf()

  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "main_feature_id")
  override var features: MutableList<FeatureData> = mutableListOf()

  fun display(): String {
    return (sequenceOf(feature.name) +

        combo(x, xa) +
        combo(y, ya) +
        combo(z, za) +

        feature.designations.asSequence()
            .mapNotNull { key ->
              val search = if (key.endsWith("*")) key.substring(0, key.length - 1) else key
              designations.find { it.designationKey == search }
            }
            .map { it.value }
            .filter { it.isNotBlank() }
            .map { it.lines()[0] }
            .filter { it.isNotBlank() } +

        feature.containFeatureTypes.asSequence()
            .flatMap { key -> features.asSequence().filter { it.feature.featureType == key.featureType } }
            .map { it.display() }

        )
        .joinToString()
  }

  private fun combo(x: Int, xa: Int) =
      if (x == 0) {
        if (xa == 0) emptySequence() else sequenceOf("0/$xa")
      } else {
        if (xa == 0) sequenceOf(f(x)) else sequenceOf("$x/$xa")
      }

  private fun f(x: Int) = when (x) {
    Int.MAX_VALUE -> "Бесконечность"
    else -> x.toString()
  }

  @Transient
  val context: (String) -> BigDecimal = {
    when (it) {
      "X" -> (x + xa).toBigDecimal()
      "Y" -> (y + ya).toBigDecimal()
      "Z" -> (z + za).toBigDecimal()
      "Н" -> skillRate(SkillType.OFFENSE)
      "З" -> skillRate(SkillType.DEFENCE)
      "О" -> skillRate(SkillType.COMMON)
      else -> throw IllegalArgumentException()
    }
  }

  private fun skillRate(type: SkillType) = if (getSkillType() == type) BigDecimal.ONE else BigDecimal.ZERO

  private fun getSkillType(): SkillType? = getOwnSkillType()
      ?: features
          .asSequence()
          .mapNotNull { it.getSkillType() }
          .first()

  private fun getOwnSkillType() = feature
      .takeIf { feature is SkillLike }
      ?.let { it as SkillLike }
      ?.skillType

  fun rate(): Decimal = features
      .map { it.rate() }
      .fold(feature.rate.toFormula(context).calculate()) { a, b -> a + b }
}