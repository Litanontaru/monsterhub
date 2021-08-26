package org.dmg.monsterhub.service

import java.math.BigDecimal


interface FNode {
  fun calculate(): BigDecimal

  operator fun unaryMinus(): FNode = FNegate(this)

  operator fun plus(right: FNode): FNode = FSum(listOf(this, right))

  operator fun minus(right: FNode): FNode = this + (-right)

  operator fun times(right: FNode): FNode = FMult(listOf(this, right))

  operator fun div(right: FNode): FNode = FDiv(this, right)

  infix fun cap(right: FNode): FNode = FCap(this, right)

  fun close(): FNode = this
}

object FNone : FNode {
  override fun calculate() = BigDecimal.ZERO

  override fun unaryMinus(): FNode = this

  override fun plus(right: FNode): FNode = right

  override fun minus(right: FNode): FNode = -right

  override fun times(right: FNode): FNode = right

  override fun div(right: FNode): FNode = FConst(BigDecimal.ONE) / right

  override fun cap(right: FNode): FNode = FConst(BigDecimal.ZERO) cap right
}

class FConst(private val value: BigDecimal) : FNode {
  override fun calculate() = value
}

class FVar(private val value: () -> BigDecimal) : FNode {
  override fun calculate() = value()
}

class FNegate(private val value: FNode) : FNode {
  override fun calculate() = -value.calculate()
}

class FSum(private val values: List<FNode>) : FNode {
  override fun calculate() = values.asSequence().map { it.calculate() }.fold(BigDecimal.ZERO) { acc, e -> acc + e }

  override fun plus(right: FNode): FNode = FSum(values + right)

  override fun times(right: FNode) = FSum(values.dropLast(1) + (values.last() * right))

  override fun div(right: FNode) = FSum(values.dropLast(1) + (values.last() / right))

  override fun cap(right: FNode) = FSum(values.dropLast(1) + (values.last() cap right))

  override fun close(): FNode = FVar { calculate() }
}

class FMult(private val values: List<FNode>) : FNode {
  override fun calculate() = values.asSequence().map { it.calculate() }.fold(BigDecimal.ONE) { acc, e -> acc * e }

  override fun times(right: FNode): FNode = FMult(values + right)
}

class FDiv(private val left: FNode, private val right: FNode) : FNode {
  override fun calculate() = left.calculate() / right.calculate()
}

class FCap(private val value: FNode, private val cap: FNode) : FNode {
  override fun calculate() =
      value.calculate().let { v ->
        cap.calculate().let {
          when {
            v > it -> it
            else -> v
          }
        }
      }
}

object Formula {
  private val PATTERN = "(\\d+(.\\d+)?)|X|Y|Z|-|\\+|\\*|/|\\(|\\)|\\|".toRegex()
  private val ZERO = FConst(BigDecimal.ZERO)

  operator fun invoke(value: String, context: (String) -> BigDecimal): FNode {
    if (value.isBlank()) {
      return ZERO
    }
    val parts = PATTERN.findAll(value.toUpperCase()).toList()
    if (parts.isEmpty()) {
      return ZERO
    }
    var i = 0

    val times: (FNode, FNode) -> FNode = { a, b -> a * b }

    fun parse(): FNode {
      var result: FNode = FNone
      var action = times

      while (true) {
        val part = parts[i].value
        i++

        if (part in setOf("X", "Y", "Z")) {
          result = action(result, FVar { context(part) })
          action = times
        } else if (part.toBigDecimalOrNull() != null) {
          result = action(result, FConst(part.toBigDecimal()))
          action = times
        } else if (part == "+") {
          action = { a, b -> a + b }
        } else if (part == "-") {
          action = { a, b -> a - b }
        } else if (part == "*") {
          action == times
        } else if (part == "/") {
          action = { a, b -> a / b }
        } else if (part == "|") {
          action = { a, b -> a cap b }
        } else if (part == "(") {
          result = action(result, parse().close())
        } else if (part == ")") {
          return result
        }

        if (i == parts.size) {
          return result
        }
      }
    }

    return parse()
  }

  @JvmStatic
  fun main(args: Array<String>) {
    val x = BigDecimal("2.0")

    listOf(
        "1",
        "1.0",
        "1 + X",
        "1 + xX",
        "1 + XYZ",
        "1 + X / 2",
        "1 + 2 + 3",
        "1 * 2 * 3",
        "1 + 2 * 3",
        "(1 + 2) * 3",
        "1 + 3x",
        "1 + 2 - 3",
        "1 + x|1",
        "1 + (x|10)/10"
    )
        .forEach { println(it + " = " + Formula(it) { x }.calculate()) }
  }
}