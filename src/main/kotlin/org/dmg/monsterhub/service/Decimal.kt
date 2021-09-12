package org.dmg.monsterhub.service

import java.math.BigDecimal

open class Decimal(val value: BigDecimal, val type: DecimalType) : Comparable<Decimal> {
  open operator fun unaryMinus(): Decimal = Decimal(-value, type)
  open operator fun plus(right: Decimal) = Decimal(value + right.value, type + right.type)
  open operator fun minus(right: Decimal) = Decimal(value - right.value, type + right.type)
  open operator fun times(right: Decimal) = Decimal(value * right.value, type * right.type)
  open operator fun div(right: Decimal) = Decimal(BigDecimal("1.0") * value / right.value, type / right.type)

  override operator fun compareTo(other: Decimal) = when (type) {
    other.type -> value.compareTo(other.value)
    else -> throw IllegalStateException("Cannot compare $type and ${other.type}")
  }

  fun isNotBlank() = value.compareTo(BigDecimal.ZERO) == 0 || type != DecimalType.DIGIT

  open infix fun cast(type: DecimalType) = Decimal(value, this.type + type)

  override fun toString(): String = type display value

  fun toInt(): Int = value.toInt()

  companion object {
    val NONE = NoneDecimal(DecimalType.DIGIT)
    val ZERO = Decimal(BigDecimal.ZERO, DecimalType.DIGIT)
    val ONE = Decimal(BigDecimal.ONE, DecimalType.DIGIT)
  }
}

class NoneDecimal(type: DecimalType) : Decimal(BigDecimal.ZERO, type) {
  override fun unaryMinus() = this

  override fun plus(right: Decimal) = right

  override fun minus(right: Decimal) = -right

  override fun times(right: Decimal) = right

  override fun div(right: Decimal) = ONE / right

  override fun cast(type: DecimalType) = NoneDecimal(type)
}

fun BigDecimal.toDecimal(): Decimal = Decimal(this, DecimalType.DIGIT)

fun Int.toDecimal(): Decimal = this.toBigDecimal().toDecimal()

enum class DecimalType(val value: String, val display: (BigDecimal) -> String) {
  DIGIT("", { it.toInt().toString() }),
  PE("ПЭ", { it.toInt().toString() + " ПЭ" }),
  PC("ПК", { it.toInt().toString() + " ПК" }),
  MULT("×", { "× " + it.toString() });

  infix fun display(digit: BigDecimal): String = display.invoke(digit)

  operator fun plus(right: DecimalType) = when (this) {
    DIGIT -> right
    else -> when (right) {
      DIGIT -> this
      this -> this
      else -> throw IllegalArgumentException("Cannot combine $this and $right")
    }
  }

  operator fun times(right: DecimalType) = when (this) {
    DIGIT, MULT -> right
    else -> when (right) {
      DIGIT, MULT -> this
      else -> throw IllegalArgumentException("Cannot combine $this and $right")
    }
  }

  operator fun div(right: DecimalType) = when (right) {
    DIGIT, MULT -> this
    else -> throw IllegalArgumentException("Cannot divide on $right")
  }

  companion object {
    operator fun invoke(value: String): DecimalType {
      return values().find { it.value == value } ?: DIGIT
    }
  }
}