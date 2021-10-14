package org.dmg.monsterhub.data.meta

import org.dmg.isZero
import java.math.BigDecimal

enum class NumberOption(val displayName: String, val format: (BigDecimal, BigDecimal, BigDecimal) -> Sequence<String>) {
  NONE("Нет", { _, _, _ -> none() }),
  POSITIVE("Позитивные", { x, _, _ -> positive(x) }),
  POSITIVE_AND_INFINITE("Позитивные и бесконечность", { x, _, _ -> positiveAndInfinite(x) }),
  FREE("Любые целые", { x, _, _ -> free(x) }),
  DAMAGE("Урон", { x, xa, _ -> damage(x, xa) }),
  ARMOR("Очки брони", { x, xa, xb -> armor(x, xa, xb) }),
  IMPORTANCE("Важность", { x, _, _ -> importance(x) });

  companion object {
    val IMPORTANCE_OPTIONS = listOf(
        "Никогда или Никакую роль",
        "Малую Редко",
        "Важную Редко",
        "Малую Вероятно",
        "Эпическую Редко",
        "Малую Часто",
        "Важную Вероятно",
        "Важную Часто",
        "Эпическую Вероятно",
        "Эпическую Часто"
    )

    val display = values().asSequence().map { it.displayName }.toList()

    operator fun invoke(displayName: String): NumberOption = values().find { it.displayName == displayName } ?: NONE

    private fun seq(x: BigDecimal): Sequence<String> = sequenceOf(x.stripTrailingZeros().toPlainString())

    private fun none() = sequenceOf<String>()

    private fun positive(x: BigDecimal) = if (x.isZero()) sequenceOf() else seq(x)

    private fun positiveAndInfinite(x: BigDecimal) = when {
      x.isZero() -> sequenceOf()
      x.toInt() == Int.MAX_VALUE -> sequenceOf("Бесконечность")
      else -> seq(x)
    }

    private fun free(x: BigDecimal) = seq(x)

    private fun damage(x: BigDecimal, xa: BigDecimal) = when {
      x.isZero() && xa.isZero() -> sequenceOf()
      else -> sequenceOf("${x.stripTrailingZeros().toPlainString()}/${xa.stripTrailingZeros().toPlainString()}")
    }

    private fun armor(x: BigDecimal, xa: BigDecimal, xb: BigDecimal) = when {
      x.isZero() && xa.isZero() && xb.isZero() -> sequenceOf()
      else -> sequenceOf("${x.stripTrailingZeros().toPlainString()}/${xa.stripTrailingZeros().toPlainString()}/${xb.stripTrailingZeros().toPlainString()}")
    }

    private fun importance(x: BigDecimal) = sequenceOf(IMPORTANCE_OPTIONS[x.toInt()])
  }
}