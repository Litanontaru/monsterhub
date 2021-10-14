package org.dmg.monsterhub.data

import org.dmg.isZero
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.meta.FeatureContainerItem
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
  var xb: BigDecimal = BigDecimal.ZERO
  var y: BigDecimal = BigDecimal.ZERO
  var ya: BigDecimal = BigDecimal.ZERO
  var yb: BigDecimal = BigDecimal.ZERO
  var z: BigDecimal = BigDecimal.ZERO
  var za: BigDecimal = BigDecimal.ZERO
  var zb: BigDecimal = BigDecimal.ZERO

  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "feature_data_id")
  var designations: MutableList<FeatureDataDesignation> = mutableListOf()

  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "main_feature_id")
  override var features: MutableList<FeatureData> = mutableListOf()

  override fun meta(): List<FeatureContainerItem> = feature.containFeatureTypes

  fun display(): String {
    return (sequenceOf(feature.name) +
        configuration() +
        feature.containFeatureTypes.asSequence()
            .flatMap { key -> features.asSequence().filter { it.feature.featureType == key.featureType } }
            .map { it.display() }

        )
        .joinToString()
  }

  fun shortDisplay() = (sequenceOf(feature.name) + configuration()).joinToString()

  fun displayConfig() = configuration().joinToString()

  private fun configuration(): Sequence<String> =
      feature.x.format(x, xa, xb) +
          feature.y.format(y, ya, yb) +
          feature.z.format(z, za, zb) +

          feature.designations.asSequence()
              .mapNotNull { key ->
                val search = if (key.endsWith("*")) key.substring(0, key.length - 1) else key
                designations.find { it.designationKey == search }
              }
              .map { it.value }
              .filter { it.isNotBlank() }
              .map { it.lines()[0] }
              .filter { it.isNotBlank() }

  private fun combo(x: BigDecimal, xa: BigDecimal, xb: BigDecimal) =
      (when {
        x.isZero() -> listOf(x, xa, xb)
            .takeIf { it.any { !it.isZero() } }
            ?.let { it.asSequence().map { it } }
            ?: emptySequence()
        xa.isZero() && xb.isZero() -> sequenceOf(x)
        xb.isZero() -> sequenceOf(x, xa)
        else -> sequenceOf(x, xa, xb)
      }).map { it.stripTrailingZeros().toPlainString() }

  private fun f(x: BigDecimal) = when (x) {
    Int.MAX_VALUE.toBigDecimal() -> "Бесконечность"
    else -> x.toPlainString()
  }

  @Transient
  val context: (String) -> List<BigDecimal> = {
    when (it) {
      "X" -> listOf(x, xa, xb)
      "Y" -> listOf(y, ya, yb)
      "Z" -> listOf(z, za, zb)
      "Н" -> listOf(skillRate(SkillType.OFFENSE))
      "З" -> listOf(skillRate(SkillType.DEFENCE))
      "О" -> listOf(skillRate(SkillType.COMMON))
      "R" -> listOf(features.map { it.feature.rate().value }.fold(BigDecimal.ZERO, { a, b -> a + b }))
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
      .fold(feature.rate.toFormula(context).calculateFinal()) { a, b -> a + b }
}