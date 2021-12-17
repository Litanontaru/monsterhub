package org.dmg.monsterhub.data

import org.dmg.monsterhub.service.Decimal
import org.dmg.monsterhub.service.DecimalType
import org.dmg.monsterhub.service.toDecimal
import java.math.BigDecimal
import java.math.RoundingMode

enum class PowerRateCalculator(val display: String, val calculator: (Power) -> Decimal) {
  STANDARD("Обычный", { it.standardRate() }),
  ACQUISITION("Приобретение", { it.acquisitionRate() }),
  COMPOSITION("Совмещение", { it.compositionRate() }),
  ALT("Альтернатива", { it.alternativeRate() }),
  AREA("Область", { it.areaRate() })
}

private fun Power.standardRate() = (power() + effect() * multiplier() - compensation()).toDecimal()

private fun Power.acquisitionRate() = (
    6.toBigDecimal() + power() + effect() +
        (minorRates().singleOrNull()?.value ?: BigDecimal.ZERO) -
        (compensation() / multiplier())
    ).toDecimal()

private fun Power.compositionRate(): Decimal {
  val compensation = compensation()

  val rates = minorRates().map { it.value }
  val max = rates.maxBy { it } ?: return -compensation.toDecimal()

  return ((rates.sum() + max) * BigDecimal(0.5) - compensation).setScale(0, RoundingMode.UP).toDecimal()
}

private fun Power.alternativeRate(): Decimal {
  val compensation = compensation()
  val rates = minorRates().map { it.value }

  return rates.maxBy { it }
      ?.let {
        (it + rates.size.toBigDecimal() - BigDecimal.ONE - compensation).toDecimal()
      }
      ?: -compensation.toDecimal()
}

private fun Power.areaRate() = (power() + (minorRates().map { it.value }.sum()) * minorMultiplier() - compensation()).toDecimal()

private fun Power.multiplier() = features
    .map { it.rate() }
    .filter { it.type == DecimalType.MULT }
    .map { it.value }
    .fold(BigDecimal.ONE) { a, b -> a * b }

private fun Power.compensation() = features
    .map { it.rate() }
    .filter { it.type == DecimalType.PC }
    .map { it.value }
    .sum()

private fun Power.effect() = features
    .map { it.rate() }
    .filter { it.type == DecimalType.PE }
    .map { it.value }
    .sum()

private fun Power.power() = features
    .map { it.rate() }
    .filter { it.type == DecimalType.PS }
    .map { it.value }
    .sum()

private fun Power.minors() = features
    .find { it.feature is PowerEffect }
    ?.features
    ?: emptyList<FeatureData>()

private fun Power.minorRates() = minors().map { it.feature.rate() }

private fun Power.minorMultiplier() = minors()
    .map { it.rate() }
    .filter { it.type == DecimalType.MULT }
    .map { it.value }
    .fold(BigDecimal.ONE) { a, b -> a * b }

private fun List<BigDecimal>.sum() = this.fold(BigDecimal.ZERO) { a, b -> a + b }