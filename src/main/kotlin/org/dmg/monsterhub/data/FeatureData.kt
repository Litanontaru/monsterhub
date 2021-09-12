package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.service.Decimal
import org.dmg.monsterhub.service.Formula.toFormula
import java.math.BigDecimal
import javax.persistence.*

@Entity
class FeatureData : DBObject(), FeatureContainerData {
  @ManyToOne
  @JoinColumn(name = "feature_id", nullable = true)
  lateinit var feature: Feature

  var x: BigDecimal = BigDecimal.ZERO
  var xa: BigDecimal = BigDecimal.ZERO
  var y: BigDecimal = BigDecimal.ZERO
  var ya: BigDecimal = BigDecimal.ZERO
  var z: BigDecimal = BigDecimal.ZERO
  var za: BigDecimal = BigDecimal.ZERO

  @OneToMany(orphanRemoval = true, cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
  @JoinColumn(name = "feature_data_id")
  var designations: MutableList<FeatureDataDesignation> = mutableListOf()

  @OneToMany(orphanRemoval = true, cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
  @JoinColumn(name = "main_feature_id")
  override var features: MutableList<FeatureData> = mutableListOf()

  fun display(): String {
    return (sequenceOf(feature.name) +

        combo(x.stripTrailingZeros(), xa.stripTrailingZeros()) +
        combo(y.stripTrailingZeros(), ya.stripTrailingZeros()) +
        combo(z.stripTrailingZeros(), za.stripTrailingZeros()) +

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

  private fun combo(x: BigDecimal, xa: BigDecimal) =
      if (x.compareTo(BigDecimal.ZERO) == 0) {
        if (xa.compareTo(BigDecimal.ZERO) == 0) emptySequence() else sequenceOf("0/$xa")
      } else {
        if (xa.compareTo(BigDecimal.ZERO) == 0) sequenceOf(f(x)) else sequenceOf("$x/$xa")
      }

  private fun f(x: BigDecimal) = when (x) {
    Int.MAX_VALUE.toBigDecimal() -> "Бесконечность"
    else -> x.toString()
  }

  @Transient
  val context: (String) -> BigDecimal = {
    when (it) {
      "X" -> (x + xa)
      "Y" -> (y + ya)
      "Z" -> (z + za)
      "Н" -> skillRate(SkillType.OFFENSE)
      "З" -> skillRate(SkillType.DEFENCE)
      "О" -> skillRate(SkillType.COMMON)
      "R" -> features.map { it.feature.rate().value }.fold(BigDecimal.ZERO, { a, b -> a + b })
      else -> throw IllegalArgumentException()
    }
  }

  private fun skillRate(type: SkillType) = if (getSkillType() == type) BigDecimal.ONE else BigDecimal.ZERO

  private fun getSkillType(): SkillType? = getOwnSkillType()
      ?: features
          .asSequence()
          .mapNotNull { it.getSkillType() }
          .take(1)
          .singleOrNull()

  private fun getOwnSkillType() = feature
      .takeIf { feature is SkillLike }
      ?.let { it as SkillLike }
      ?.skillType

  fun rate(): Decimal = features
      .map { it.rate() }
      .fold(feature.rate.toFormula(context).calculate()) { a, b -> a + b }
}