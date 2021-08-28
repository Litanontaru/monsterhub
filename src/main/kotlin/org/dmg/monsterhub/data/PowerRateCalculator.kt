package org.dmg.monsterhub.data

import org.dmg.monsterhub.service.Decimal
import org.dmg.monsterhub.service.DecimalType
import org.dmg.monsterhub.service.toDecimal
import java.math.BigDecimal

enum class PowerRateCalculator(val display: String, val calculator: (Power) -> Decimal) {
  STANDARD("Обычный", { it.standardRate() })
}

private fun Power.standardRate() = (effect() * multiplier() - compensation()).toDecimal()

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