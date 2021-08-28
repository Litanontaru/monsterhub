package org.dmg.monsterhub.data

import org.dmg.monsterhub.service.Decimal
import org.dmg.monsterhub.service.DecimalType
import org.dmg.monsterhub.service.toDecimal
import java.math.BigDecimal

enum class PowerRateCalculator(val display: String, val calculator: (Power) -> Decimal) {
  STANDARD("Обычный", { it.standardRate() }),
  ACQUISITION("Приобретение", { it.acquisitionRate() }),
  COMPOSITION("Совмещение", { it.compositionRate() })
}

private fun Power.standardRate() = (effect() * multiplier() - compensation()).toDecimal()

private fun Power.acquisitionRate() = (6.toBigDecimal() + minorRates().single().value - (compensation() / multiplier())).toDecimal()

private fun Power.compositionRate(): Decimal {
  val compensation = compensation()

  val rates = minorRates().map { it.value }
  val max = rates.maxBy { it } ?: return -compensation.toDecimal()

  var i = BigDecimal.ZERO
  var sum = BigDecimal.ZERO

  while (true) {
    i++
    val addition = rates.filter { it > max - i }.count().toBigDecimal()
    if (sum + addition > compensation) {
      return (max - i).toDecimal()
    }
    sum += addition
  }
}

private fun Power.multiplier() = features
    .map { it.rate() }
    .filter { it.type == DecimalType.MULT }
    .map { it.value }
    .fold(BigDecimal.ONE) { a, b -> a * b }

private fun Power.compensation() = features
    .map { it.rate() }
    .filter { it.type == DecimalType.PC }
    .map { it.value }
    .fold(BigDecimal.ZERO) { a, b -> a + b }

private fun Power.effect() = features
    .map { it.rate() }
    .filter { it.type == DecimalType.PE }
    .map { it.value }
    .fold(BigDecimal.ZERO) { a, b -> a + b }

private fun Power.minors() = features
    .find { it.feature is PowerEffect }
    ?.features
    ?: emptyList<FeatureData>()

private fun Power.minorRates() = minors().map { it.feature.rate() }