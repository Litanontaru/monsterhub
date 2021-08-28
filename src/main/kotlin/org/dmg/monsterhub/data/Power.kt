package org.dmg.monsterhub.data

import org.dmg.monsterhub.service.DecimalType
import org.dmg.monsterhub.service.toDecimal
import java.math.BigDecimal
import javax.persistence.Entity

@Entity
class Power : ContainerData() {
  private fun multiplier() = features
      .map { it.rate() }
      .filter { it.type == DecimalType.MULT }
      .map { it.value }
      .fold(BigDecimal.ONE) { a, b -> a * b }

  private fun compensation() = features
      .map { it.rate() }
      .filter { it.type == DecimalType.PC }
      .map { it.value }
      .fold(BigDecimal.ZERO) { a, b -> a + b }

  private fun effect() = features
      .map { it.rate() }
      .filter { it.type == DecimalType.PE }
      .map { it.value }
      .fold(BigDecimal.ZERO) { a, b -> a + b }

  override fun rate() = (effect() * multiplier() - compensation()).toDecimal()
}